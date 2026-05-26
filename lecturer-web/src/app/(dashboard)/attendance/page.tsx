"use client";
import React, { useState, useEffect, useCallback, useRef } from "react";
import { QRCodeSVG } from "qrcode.react";
import api from "@/lib/api";
import type { AttendanceSession, Attendance } from "@/types";

// ── QR Countdown Timer ────────────────────────────────────────
function QRTimer({ expiresAt, onExpire }: { expiresAt: string; onExpire: () => void }) {
  const [remaining, setRemaining] = useState(5);
  useEffect(() => {
    const interval = setInterval(() => {
      const diff = Math.ceil((new Date(expiresAt).getTime() - Date.now()) / 1000);
      if (diff <= 0) { onExpire(); clearInterval(interval); }
      else setRemaining(diff);
    }, 200);
    return () => clearInterval(interval);
  }, [expiresAt, onExpire]);
  return (
    <div className="flex items-center gap-2 mt-3">
      <div className={`w-8 h-8 rounded-full flex items-center justify-center font-bold text-sm ${remaining <= 2 ? "bg-error text-on-error" : "bg-primary text-on-primary"}`}>
        {remaining}
      </div>
      <span className="text-body-sm text-on-surface-variant">giây còn lại</span>
      <div className="flex-1 h-1.5 bg-surface-container-high rounded-full overflow-hidden">
        <div
          className={`h-full rounded-full transition-all ${remaining <= 2 ? "bg-error" : "bg-primary"}`}
          style={{ width: `${(remaining / 5) * 100}%` }}
        />
      </div>
    </div>
  );
}

// ── Status Badge ──────────────────────────────────────────────
function StatusBadge({ status }: { status: Attendance["status"] }) {
  const map = {
    present: { label: "Có mặt", cls: "bg-secondary-container text-on-secondary-container", icon: "check_circle" },
    absent: { label: "Vắng", cls: "bg-error-container text-on-error-container", icon: "cancel" },
    late: { label: "Trễ", cls: "bg-tertiary-container/50 text-tertiary", icon: "schedule" },
    excused: { label: "Có phép", cls: "bg-surface-container-high text-on-surface-variant", icon: "event_available" },
  };
  const s = map[status] ?? map.absent;
  return (
    <span className={`inline-flex items-center gap-1 px-2 py-0.5 rounded-full text-label ${s.cls}`}>
      <span className="material-symbols-outlined text-[12px]" style={{ fontVariationSettings: "'FILL' 1" }}>{s.icon}</span>
      {s.label}
    </span>
  );
}

// ── Skeleton ──────────────────────────────────────────────────
function Skeleton() {
  return (
    <div className="animate-pulse space-y-3">
      {[...Array(4)].map((_, i) => (
        <div key={i} className="flex items-center gap-4 p-md bg-surface-container-low rounded-lg">
          <div className="w-8 h-8 rounded-full bg-surface-container-high" />
          <div className="flex-1 space-y-2">
            <div className="h-3 bg-surface-container-high rounded w-1/3" />
            <div className="h-3 bg-surface-container-high rounded w-1/5" />
          </div>
          <div className="h-6 w-16 bg-surface-container-high rounded-full" />
        </div>
      ))}
    </div>
  );
}

// ── Main ──────────────────────────────────────────────────────
export default function AttendancePage() {
  // Session state
  const [session, setSession] = useState<AttendanceSession | null>(null);
  const [isSessionLoading, setIsSessionLoading] = useState(false);
  const [isStarted, setIsStarted] = useState(false);
  const [showStartModal, setShowStartModal] = useState(false);
  const [startConfig, setStartConfig] = useState({ topic: "Điểm danh thường kỳ", duration: "15" });

  // QR state
  const [qrToken, setQrToken] = useState<string | null>(null);
  const [qrExpiresAt, setQrExpiresAt] = useState<string | null>(null);
  const [isRefreshingQR, setIsRefreshingQR] = useState(false);

  // Attendance list
  const [attendances, setAttendances] = useState<Attendance[]>([]);
  const [isListLoading, setIsListLoading] = useState(false);
  const [filterTab, setFilterTab] = useState<"all" | "absent">("all");

  // Stats from mock (replace with real data from session)
  const totalStudents = attendances.length || 65;
  const presentCount = attendances.filter((a) => a.status === "present").length;
  const attendanceRate = totalStudents > 0 ? Math.round((presentCount / totalStudents) * 100) : 0;

  const pollingRef = useRef<ReturnType<typeof setInterval> | null>(null);

  // ── Refresh QR token ──
  const refreshQR = useCallback(async (sessionId: string) => {
    setIsRefreshingQR(true);
    try {
      const { data } = await api.post<{ qrToken: string; expiresAt: string }>(
        `/attendance/sessions/${sessionId}/refresh-qr`
      );
      setQrToken(data.qrToken);
      setQrExpiresAt(data.expiresAt);
    } catch {
      // silently retry
    } finally {
      setIsRefreshingQR(false);
    }
  }, []);

  // ── Fetch attendance list ──
  const fetchAttendances = useCallback(async (sessionId: string) => {
    setIsListLoading(true);
    try {
      const { data } = await api.get<Attendance[]>(`/attendance/sessions/${sessionId}/attendances`);
      setAttendances(data);
    } catch {
      // silently fail
    } finally {
      setIsListLoading(false);
    }
  }, []);

  // ── Start session ──
  const handleStartSession = async (e?: React.FormEvent) => {
    if (e) e.preventDefault();
    setIsSessionLoading(true);
    setShowStartModal(false);
    try {
      // classId would come from route params in a real app
      const classId = "demo-class-id";
      const { data } = await api.post<AttendanceSession>("/attendance/sessions", { classId });
      setSession(data);
      setQrToken(data.qrToken ?? null);
      setQrExpiresAt(data.qrExpiresAt ?? null);
      setIsStarted(true);

      // Poll attendance list every 3s
      pollingRef.current = setInterval(() => fetchAttendances(data.id), 3000);
      fetchAttendances(data.id);
    } catch {
      // handle error
    } finally {
      setIsSessionLoading(false);
    }
  };

  // ── End session ──
  const handleEndSession = async () => {
    if (!session) return;
    try {
      await api.patch(`/attendance/sessions/${session.id}/close`);
    } catch { /* ignore */ }
    if (pollingRef.current) clearInterval(pollingRef.current);
    setIsStarted(false);
    setSession(null);
    setQrToken(null);
    setQrExpiresAt(null);
  };

  // ── Manual attendance toggle ──
  const handleToggleAttendance = async (studentId: string, current: Attendance["status"]) => {
    if (!session) return;
    const newStatus: Attendance["status"] = current === "present" ? "absent" : "present";
    try {
      await api.patch(`/attendance/sessions/${session.id}/students/${studentId}`, { status: newStatus });
      setAttendances((prev) =>
        prev.map((a) => (a.studentId === studentId ? { ...a, status: newStatus } : a))
      );
    } catch { /* silently fail */ }
  };

  // ── Save attendance ──
  const handleSave = async () => {
    if (!session) return;
    try {
      await api.post(`/attendance/sessions/${session.id}/save`);
    } catch { /* handle */ }
  };

  useEffect(() => {
    return () => {
      if (pollingRef.current) clearInterval(pollingRef.current);
    };
  }, []);

  const filteredAttendances = filterTab === "absent"
    ? attendances.filter((a) => a.status === "absent")
    : attendances;

  // ── Mock data for display when session not started ──
  const mockStudents = [
    { stt: 1, mssv: "20201234", name: "Nguyễn Văn An", sessions: ["present","present","present","absent"], current: "present" as const, percent: "80%" },
    { stt: 2, mssv: "20205678", name: "Trần Thị Bích", sessions: ["present","present","present","present"], current: "present" as const, percent: "100%" },
    { stt: 3, mssv: "20209012", name: "Lê Công Quốc", sessions: ["present","absent","absent","present"], current: "absent" as const, percent: "40%" },
    { stt: 4, mssv: "20203456", name: "Phạm Hữu Dũng", sessions: ["present","present","present","present"], current: "present" as const, percent: "100%" },
  ];

  return (
    <main className="flex-1 overflow-y-auto p-xl bg-background">
      {/* Page Header */}
      <div className="flex flex-col md:flex-row justify-between items-start md:items-center mb-lg gap-4">
        <div>
          <h2 className="font-h1 text-h1 text-on-background">Cấu trúc dữ liệu &amp; Giải thuật</h2>
          <div className="flex items-center gap-2 mt-2 text-on-surface-variant font-body-sm text-body-sm">
            <span className="bg-surface-variant px-2 py-1 rounded">Mã HP: IT3011</span>
            <span className="text-outline">•</span><span>Nhóm: 02</span>
            <span className="text-outline">•</span><span>Học kỳ: 2023.2</span>
          </div>
        </div>
        <div className="flex items-center gap-3">
          {!isStarted ? (
            <button
              onClick={() => setShowStartModal(true)}
              disabled={isSessionLoading}
              className="flex items-center gap-2 font-button text-button px-4 py-2 bg-primary text-on-primary hover:bg-primary/90 rounded-lg transition-colors shadow-sm disabled:opacity-60"
            >
              {isSessionLoading
                ? <span className="material-symbols-outlined text-[18px] animate-spin">progress_activity</span>
                : <span className="material-symbols-outlined text-[18px]">qr_code_scanner</span>}
              Mở phiên điểm danh
            </button>
          ) : (
            <>
              <button
                onClick={handleSave}
                className="flex items-center gap-2 font-button text-button px-4 py-2 bg-primary-fixed text-on-primary-fixed hover:bg-primary-fixed-dim rounded-lg transition-colors"
              >
                <span className="material-symbols-outlined text-[18px]">save</span>
                Lưu điểm danh
              </button>
              <button
                onClick={handleEndSession}
                className="flex items-center gap-2 font-button text-button px-4 py-2 bg-error-container text-on-error-container hover:bg-error-container/80 rounded-lg transition-colors"
              >
                <span className="material-symbols-outlined text-[18px]">stop_circle</span>
                Đóng phiên
              </button>
            </>
          )}
          <button className="flex items-center gap-2 font-button text-button px-4 py-2 border border-outline-variant text-on-surface hover:bg-surface-container-low rounded-lg transition-colors">
            <span className="material-symbols-outlined text-[18px]">download</span>
            Xuất báo cáo
          </button>
        </div>
      </div>

      {/* Bento Grid */}
      <div className="grid grid-cols-12 gap-container_gutter mb-xl">
        {/* Stats Card */}
        <div className="col-span-12 md:col-span-4 bg-surface-container-lowest rounded-xl shadow-[var(--shadow-card)] p-lg flex flex-col justify-between border border-outline-variant/30">
          <div>
            <h3 className="font-h3 text-h3 text-on-surface mb-6">Tổng quan sỉ số</h3>
            <div className="flex items-center gap-3 mb-6">
              <div className="w-10 h-10 rounded-full bg-primary-fixed flex items-center justify-center text-primary">
                <span className="material-symbols-outlined">groups</span>
              </div>
              <div>
                <p className="font-label text-label text-on-surface-variant">TỔNG SINH VIÊN</p>
                <p className="font-h2 text-h2 text-on-surface">{totalStudents}</p>
              </div>
            </div>
            <div className="flex items-center justify-between mb-2">
              <span className="font-body-sm text-body-sm text-on-surface-variant">Tỉ lệ đi học trung bình</span>
              <span className="font-label text-label text-primary">{isStarted ? `${attendanceRate}%` : "92%"}</span>
            </div>
            <div className="w-full h-2 bg-surface-container-highest rounded-full overflow-hidden">
              <div className="h-full bg-primary rounded-full transition-all" style={{ width: isStarted ? `${attendanceRate}%` : "92%" }} />
            </div>
          </div>
          <div className="mt-6 pt-4 border-t border-surface-variant flex items-center justify-between font-body-sm text-body-sm">
            <span className="text-on-surface-variant">Buổi học hiện tại:</span>
            <span className="font-bold text-on-surface">Buổi 5 (12/10/2023)</span>
          </div>
        </div>

        {/* QR Code Card */}
        <div className="col-span-12 md:col-span-8 bg-surface-container-lowest rounded-xl shadow-[var(--shadow-card)] p-lg border border-outline-variant/30">
          {isStarted && qrToken ? (
            <div className="flex flex-col items-center">
              <div className="flex items-center gap-2 mb-4 self-start">
                <div className="w-2.5 h-2.5 rounded-full bg-secondary animate-pulse" />
                <span className="text-body-sm text-secondary font-semibold">Phiên điểm danh đang hoạt động</span>
              </div>
              <div className="p-4 bg-white rounded-xl border-2 border-primary/20 shadow-inner">
                <QRCodeSVG
                  value={qrToken}
                  size={180}
                  bgColor="#ffffff"
                  fgColor="#4d41df"
                  level="H"
                  includeMargin={false}
                />
              </div>
              <p className="text-body-sm text-on-surface-variant mt-3 text-center">
                Sinh viên quét mã QR để điểm danh
              </p>
              {qrExpiresAt && (
                <div className="w-full max-w-[220px]">
                  <QRTimer
                    expiresAt={qrExpiresAt}
                    onExpire={() => session && refreshQR(session.id)}
                  />
                </div>
              )}
              {isRefreshingQR && (
                <p className="text-label text-primary mt-1">Đang làm mới mã QR...</p>
              )}
            </div>
          ) : (
            <div className="h-full flex flex-col items-center justify-center gap-4 py-8">
              <div className="w-24 h-24 rounded-2xl bg-surface-container flex items-center justify-center">
                <span className="material-symbols-outlined text-[48px] text-outline" style={{ fontVariationSettings: "'FILL' 1" }}>qr_code_2</span>
              </div>
              <div className="text-center">
                <p className="text-h3 text-on-surface">Mã QR điểm danh động</p>
                <p className="text-body-sm text-on-surface-variant mt-1">
                  Nhấn "Mở phiên điểm danh" để tạo mã QR<br/>
                  Mã tự động làm mới mỗi <strong>5 giây</strong>
                </p>
              </div>
            </div>
          )}
        </div>
      </div>

      {/* Student List */}
      <div className="bg-surface-container-lowest rounded-xl shadow-[var(--shadow-card)] border border-outline-variant/30 overflow-hidden">
        <div className="p-lg border-b border-surface-variant flex justify-between items-center">
          <h3 className="font-h3 text-h3 text-on-surface">Danh sách sinh viên</h3>
          <div className="flex items-center bg-surface-container-low rounded-lg p-1">
            <button onClick={() => setFilterTab("all")} className={`px-3 py-1 font-body-sm text-body-sm rounded transition-colors ${filterTab === "all" ? "bg-surface-container-lowest shadow-sm text-on-surface font-semibold" : "text-on-surface-variant hover:text-on-surface"}`}>Tất cả</button>
            <button onClick={() => setFilterTab("absent")} className={`px-3 py-1 font-body-sm text-body-sm rounded transition-colors ${filterTab === "absent" ? "bg-surface-container-lowest shadow-sm text-on-surface font-semibold" : "text-on-surface-variant hover:text-on-surface"}`}>
              Vắng mặt {attendances.filter((a) => a.status === "absent").length > 0 && `(${attendances.filter((a) => a.status === "absent").length})`}
            </button>
          </div>
        </div>
        <div className="overflow-x-auto">
          {isStarted && isListLoading ? (
            <div className="p-lg"><Skeleton /></div>
          ) : isStarted && attendances.length > 0 ? (
            <table className="w-full text-left border-collapse">
              <thead>
                <tr className="bg-surface-container-low border-b border-surface-variant">
                  <th className="py-3 px-6 font-label text-label text-on-surface-variant text-center w-16">STT</th>
                  <th className="py-3 px-6 font-label text-label text-on-surface-variant min-w-[250px]">SINH VIÊN</th>
                  <th className="py-3 px-4 font-label text-label text-on-surface-variant text-center">TRẠNG THÁI</th>
                  <th className="py-3 px-4 font-label text-label text-on-surface-variant text-center">GIỜ ĐIỂM DANH</th>
                  <th className="py-3 px-4 font-label text-label text-on-surface-variant text-center">ĐIỀU CHỈNH</th>
                </tr>
              </thead>
              <tbody className="divide-y divide-surface-variant">
                {filteredAttendances.map((att, idx) => (
                  <tr key={att.id} className="hover:bg-surface-container-low/30 transition-colors">
                    <td className="py-3 px-6 text-center text-on-surface-variant text-body-sm">{idx + 1}</td>
                    <td className="py-3 px-6">
                      <div className="flex items-center gap-3">
                        <div className="w-8 h-8 rounded-full bg-primary-container/20 text-primary flex items-center justify-center font-bold text-sm">
                          {att.student?.name?.charAt(0) ?? "?"}
                        </div>
                        <div>
                          <p className="font-semibold text-body-sm text-on-surface">{att.student?.name ?? "—"}</p>
                          <p className="text-[11px] text-on-surface-variant">{att.student?.code ?? att.studentId}</p>
                        </div>
                      </div>
                    </td>
                    <td className="py-3 px-4 text-center"><StatusBadge status={att.status} /></td>
                    <td className="py-3 px-4 text-center text-body-sm text-on-surface-variant">
                      {att.checkedAt ? new Date(att.checkedAt).toLocaleTimeString("vi-VN") : "—"}
                    </td>
                    <td className="py-3 px-4 text-center">
                      <button
                        onClick={() => handleToggleAttendance(att.studentId, att.status)}
                        className="px-3 py-1 rounded-lg text-label border border-outline-variant hover:bg-surface-container transition-colors"
                      >
                        {att.status === "present" ? "Đánh vắng" : "Đánh có mặt"}
                      </button>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          ) : (
            // Show mock UI when session not started
            <table className="w-full text-left border-collapse">
              <thead>
                <tr className="bg-surface-container-low border-b border-surface-variant">
                  <th className="py-3 px-6 font-label text-label text-on-surface-variant text-center w-16">STT</th>
                  <th className="py-3 px-6 font-label text-label text-on-surface-variant min-w-[250px]">SINH VIÊN</th>
                  {[1,2,3,4].map(b => <th key={b} className="py-3 px-4 font-label text-label text-on-surface-variant text-center">BUỔI {b}</th>)}
                  <th className="py-3 px-4 font-label text-label text-primary bg-primary-fixed-dim/30 text-center border-x border-primary-fixed">BUỔI 5 (Hôm nay)</th>
                  <th className="py-3 px-6 font-label text-label text-on-surface-variant text-center">% ĐI HỌC</th>
                </tr>
              </thead>
              <tbody className="divide-y divide-surface-variant">
                {mockStudents.map((s) => (
                  <tr key={s.stt} className="hover:bg-surface-container-low/30 transition-colors group">
                    <td className="py-3 px-6 text-center text-on-surface-variant text-body-sm">{s.stt}</td>
                    <td className="py-3 px-6">
                      <div className="flex items-center gap-3">
                        <div className="w-8 h-8 rounded-full bg-tertiary-fixed flex items-center justify-center text-tertiary font-bold text-xs">{s.name.charAt(0)}</div>
                        <div>
                          <p className="font-semibold text-body-sm text-on-surface">{s.name}</p>
                          <p className="text-[11px] text-on-surface-variant">{s.mssv}</p>
                        </div>
                      </div>
                    </td>
                    {s.sessions.map((status, i) => (
                      <td key={i} className="py-3 px-4 text-center">
                        <span className={`material-symbols-outlined text-[20px] ${status === "present" ? "text-secondary" : "text-error"}`} style={{ fontVariationSettings: "'FILL' 1" }}>
                          {status === "present" ? "check_circle" : "cancel"}
                        </span>
                      </td>
                    ))}
                    <td className="py-3 px-4 text-center bg-primary-fixed-dim/10 border-x border-primary-fixed/30">
                      <input type="checkbox" defaultChecked={s.current === "present"} className="w-5 h-5 text-secondary border-outline-variant rounded focus:ring-secondary cursor-pointer" />
                    </td>
                    <td className={`py-3 px-6 text-center font-semibold text-body-sm ${s.percent === "100%" ? "text-secondary" : s.percent === "40%" ? "text-error" : "text-on-surface"}`}>{s.percent}</td>
                  </tr>
                ))}
              </tbody>
            </table>
          )}
        </div>
      </div>

      {/* Start Session Modal */}
      {showStartModal && (
        <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/40 backdrop-blur-sm p-4">
          <div className="bg-surface-container-lowest rounded-xl shadow-[var(--shadow-float)] border border-border-muted p-xl w-full max-w-3xl">
            <h3 className="text-h3 text-on-surface mb-md">Mở phiên điểm danh</h3>
            <form className="space-y-6" onSubmit={handleStartSession}>
              <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
                <div>
                  <label className="block text-label font-label text-on-surface-variant mb-1">Chủ đề điểm danh</label>
                  <input type="text" value={startConfig.topic} onChange={e => setStartConfig({...startConfig, topic: e.target.value})} className="w-full bg-surface border border-outline-variant rounded-lg px-4 py-2 focus:border-primary outline-none" required />
                </div>
                <div>
                  <label className="block text-label font-label text-on-surface-variant mb-1">Thời gian hiệu lực (phút)</label>
                  <input type="number" min="1" max="120" value={startConfig.duration} onChange={e => setStartConfig({...startConfig, duration: e.target.value})} className="w-full bg-surface border border-outline-variant rounded-lg px-4 py-2 focus:border-primary outline-none" required />
                </div>
              </div>
              <div className="p-4 bg-primary-container/20 rounded-lg flex items-start gap-3">
                <span className="material-symbols-outlined text-primary mt-0.5">info</span>
                <p className="text-body-sm text-on-surface-variant">Mã QR sẽ được làm mới mỗi 5 giây để tránh gian lận. Phiên điểm danh sẽ tự động đóng sau thời gian hiệu lực.</p>
              </div>
              <div className="flex gap-3 justify-end pt-4 border-t border-outline-variant/30">
                <button type="button" onClick={() => setShowStartModal(false)} className="px-4 py-2 rounded-lg border border-outline-variant hover:bg-surface-container transition-colors text-button text-on-surface">Hủy</button>
                <button type="submit" className="px-4 py-2 rounded-lg bg-primary text-on-primary hover:bg-primary/90 transition-colors text-button flex items-center gap-2">
                  <span className="material-symbols-outlined text-[18px]">play_arrow</span>
                  Bắt đầu
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </main>
  );
}
