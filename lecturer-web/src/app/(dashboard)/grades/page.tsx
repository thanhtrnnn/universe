"use client";
import React, { useState } from "react";
import api from "@/lib/api";

const initialStudents = [
  { stt: 1, mssv: "20520001", name: "Nguyễn Văn An", qt: 8.5, gk: 7.0, ck: 8.0 as number | null },
  { stt: 2, mssv: "20520015", name: "Trần Thị Bích", qt: 9.0, gk: 8.5, ck: 9.0 as number | null },
  { stt: 3, mssv: "20520102", name: "Lê Hoàng Cường", qt: 6.0, gk: 5.5, ck: null as number | null },
  { stt: 4, mssv: "20520245", name: "Phạm Mỹ Duyên", qt: 10, gk: 9.5, ck: 9.5 as number | null },
  { stt: 5, mssv: "20520311", name: "Đinh Thành Đạt", qt: 7.5, gk: 6.5, ck: 7.0 as number | null },
];

function calcTotal(qt: number, gk: number, ck: number | null) {
  if (ck === null) return null;
  return Math.round((qt * 0.2 + gk * 0.3 + ck * 0.5) * 10) / 10;
}

// Toast
function Toast({ message, type, onClose }: { message: string; type: "success" | "error"; onClose: () => void }) {
  React.useEffect(() => { const t = setTimeout(onClose, 3500); return () => clearTimeout(t); }, [onClose]);
  return (
    <div className={`fixed bottom-6 right-6 z-50 flex items-center gap-3 px-lg py-md rounded-xl shadow-lg border text-body-md font-semibold ${type === "success" ? "bg-secondary-container text-on-secondary-container border-secondary-container" : "bg-error-container text-on-error-container border-error-container"}`}>
      <span className="material-symbols-outlined text-[20px]" style={{ fontVariationSettings: "'FILL' 1" }}>{type === "success" ? "check_circle" : "error"}</span>
      {message}
    </div>
  );
}

export default function GradesPage() {
  const [students, setStudents] = useState(initialStudents);
  const [search, setSearch] = useState("");
  const [isSaving, setIsSaving] = useState(false);
  const [isPublishing, setIsPublishing] = useState(false);
  const [isPublished, setIsPublished] = useState(false);
  const [toast, setToast] = useState<{ message: string; type: "success" | "error" } | null>(null);

  const classId = "SE102.N11"; // In real app, get from route params

  const handleChange = (stt: number, field: "qt" | "gk" | "ck", val: string) => {
    setStudents(prev => prev.map(s =>
      s.stt === stt ? { ...s, [field]: val === "" ? null : parseFloat(val) } : s
    ));
  };

  const handleSave = async () => {
    setIsSaving(true);
    try {
      const payload = students.map(s => ({
        studentCode: s.mssv,
        qtScore: s.qt,
        gkScore: s.gk,
        ckScore: s.ck,
      }));
      await api.post(`/grades/classes/${classId}/bulk`, { grades: payload });
      setToast({ message: "Đã lưu điểm thành công!", type: "success" });
    } catch {
      setToast({ message: "Lưu điểm thất bại. Vui lòng thử lại.", type: "error" });
    } finally {
      setIsSaving(false);
    }
  };

  const handlePublish = async () => {
    if (!window.confirm("Bạn có chắc muốn công bố điểm? Sinh viên sẽ thấy điểm ngay lập tức.")) return;
    setIsPublishing(true);
    try {
      await api.post(`/grades/classes/${classId}/publish`);
      setIsPublished(true);
      setToast({ message: "Điểm đã được công bố thành công! Sinh viên có thể xem trên app.", type: "success" });
    } catch {
      setToast({ message: "Công bố điểm thất bại. Vui lòng thử lại.", type: "error" });
    } finally {
      setIsPublishing(false);
    }
  };

  const filtered = students.filter(s =>
    s.mssv.includes(search) || s.name.toLowerCase().includes(search.toLowerCase())
  );
  const enteredCount = students.filter(s => s.ck !== null).length;

  return (
    <>
      {toast && <Toast message={toast.message} type={toast.type} onClose={() => setToast(null)} />}
      <main className="flex-1 overflow-y-auto p-xl bg-background flex flex-col gap-lg">
      {/* Page Header */}
      <div className="flex flex-col md:flex-row md:items-end justify-between gap-md">
        <div>
          <div className="flex items-center gap-sm mb-xs">
            <span className="px-2 py-1 rounded bg-primary-fixed text-on-primary-fixed font-label text-label">Học kỳ I (2023-2024)</span>
            <span className="px-2 py-1 rounded bg-surface-container text-on-surface-variant font-label text-label">Mã Lớp: SE102.N11</span>
          </div>
          <h1 className="font-h1 text-h1 text-on-surface">Nhập &amp; Quản lý điểm số</h1>
          <p className="font-body-lg text-body-lg text-on-surface-variant mt-1">Môn học: Lập trình Web</p>
        </div>
        <div className="flex items-center gap-md">
          <button
            onClick={handleSave}
            disabled={isSaving}
            className="bg-primary-fixed text-on-primary-fixed font-button text-button h-10 px-md rounded hover:opacity-90 transition-opacity flex items-center gap-sm disabled:opacity-60"
          >
            {isSaving
              ? <span className="material-symbols-outlined text-[20px] animate-spin">progress_activity</span>
              : <span className="material-symbols-outlined text-[20px]">save</span>}
            {isSaving ? "Đang lưu..." : "Lưu tất cả"}
          </button>
          <button
            onClick={handlePublish}
            disabled={isPublishing || isPublished}
            className={`font-button text-button h-10 px-md rounded transition-opacity shadow-md flex items-center gap-sm disabled:opacity-60 ${isPublished ? "bg-secondary text-on-secondary" : "bg-primary text-on-primary hover:opacity-90"}`}
          >
            {isPublishing
              ? <span className="material-symbols-outlined text-[20px] animate-spin">progress_activity</span>
              : <span className="material-symbols-outlined text-[20px]">{isPublished ? "check_circle" : "publish"}</span>}
            {isPublishing ? "Đang công bố..." : isPublished ? "Đã công bố" : "Công bố điểm"}
          </button>
        </div>
      </div>

      {/* Search Bar */}
      <div className="bg-surface-container-lowest rounded-lg p-md border border-outline-variant flex flex-col sm:flex-row gap-md justify-between items-center shadow-sm">
        <div className="relative w-full sm:w-96">
          <span className="material-symbols-outlined absolute left-sm top-1/2 -translate-y-1/2 text-outline text-[20px]">search</span>
          <input
            className="w-full pl-xl pr-sm h-10 rounded border border-outline-variant bg-surface focus:border-primary focus:ring-1 focus:ring-primary outline-none font-body-md text-body-md text-on-surface placeholder:text-outline transition-shadow"
            placeholder="Tìm kiếm theo MSSV hoặc Họ tên..."
            value={search}
            onChange={e => setSearch(e.target.value)}
          />
        </div>
        <div className="flex items-center gap-md text-on-surface-variant font-body-md text-body-md">
          <span className="flex items-center gap-1">
            <span className="w-2 h-2 rounded-full bg-secondary block" />
            Đã nhập đủ: {enteredCount}/{students.length}
          </span>
          <div className="w-px h-4 bg-outline-variant" />
          <button className="flex items-center gap-1 hover:text-primary transition-colors">
            <span className="material-symbols-outlined text-[20px]">download</span>Xuất Excel
          </button>
        </div>
      </div>

      {/* Table */}
      <div className="bg-surface-container-lowest rounded-lg shadow-sm border border-outline-variant overflow-hidden flex-1 flex flex-col">
        <div className="overflow-x-auto flex-1">
          <table className="w-full min-w-[800px] text-left border-collapse">
            <thead className="bg-surface-container sticky top-0 z-10 border-b border-outline-variant shadow-sm">
              <tr>
                <th className="p-md font-label text-label text-on-surface-variant w-[60px] text-center">STT</th>
                <th className="p-md font-label text-label text-on-surface-variant w-[130px]">MSSV</th>
                <th className="p-md font-label text-label text-on-surface-variant min-w-[200px]">Họ và tên</th>
                <th className="p-md font-label text-label text-on-surface-variant w-[120px] text-center">Điểm QT <span className="font-body-sm text-outline font-normal block">(20%)</span></th>
                <th className="p-md font-label text-label text-on-surface-variant w-[120px] text-center">Điểm GK <span className="font-body-sm text-outline font-normal block">(30%)</span></th>
                <th className="p-md font-label text-label text-on-surface-variant w-[120px] text-center">Điểm CK <span className="font-body-sm text-outline font-normal block">(50%)</span></th>
                <th className="p-md font-label text-label text-on-surface-variant w-[120px] text-center">Tổng kết</th>
              </tr>
            </thead>
            <tbody className="font-body-md text-body-md text-on-surface">
              {filtered.map(s => {
                const total = calcTotal(s.qt, s.gk, s.ck);
                const totalColor = total === null ? "text-outline" : total >= 8.5 ? "text-secondary font-bold text-lg" : "text-on-surface font-bold text-lg";
                return (
                  <tr key={s.stt} className="border-b border-surface-variant hover:bg-surface-container-low transition-colors">
                    <td className="p-md text-center text-on-surface-variant">{s.stt}</td>
                    <td className="p-md font-semibold text-primary">{s.mssv}</td>
                    <td className="p-md">{s.name}</td>
                    {(["qt","gk"] as const).map(field => (
                      <td key={field} className="p-md text-center">
                        <input type="number" min="0" max="10" step="0.5" value={s[field] ?? ""}
                          onChange={e => handleChange(s.stt, field, e.target.value)}
                          className="w-[70px] h-9 text-center rounded border border-outline-variant bg-surface-container-lowest focus:bg-white focus:border-primary focus:ring-1 focus:ring-primary outline-none transition-all hover:border-outline" />
                      </td>
                    ))}
                    <td className="p-md text-center">
                      <input type="number" min="0" max="10" step="0.5" value={s.ck ?? ""} placeholder="-"
                        onChange={e => handleChange(s.stt, "ck", e.target.value)}
                        className={`w-[70px] h-9 text-center rounded border outline-none transition-all ${s.ck === null ? "bg-error-container text-on-error-container border-outline placeholder:text-on-error-container/60" : "border-outline-variant bg-surface-container-lowest hover:border-outline focus:bg-white focus:border-primary focus:ring-1 focus:ring-primary"}`} />
                    </td>
                    <td className={`p-md text-center ${totalColor}`}>{total !== null ? total.toFixed(1) : "-"}</td>
                  </tr>
                );
              })}
            </tbody>
          </table>
        </div>
        <div className="p-md border-t border-outline-variant bg-surface flex justify-between items-center text-on-surface-variant font-body-sm text-body-sm">
          <span>Hiển thị 1 - 5 trong tổng số 40 sinh viên</span>
          <div className="flex items-center gap-xs">
            <button className="w-8 h-8 rounded flex items-center justify-center hover:bg-surface-container transition-colors opacity-40" disabled>
              <span className="material-symbols-outlined text-[20px]">chevron_left</span>
            </button>
            <button className="w-8 h-8 rounded flex items-center justify-center bg-primary text-on-primary font-semibold">1</button>
            <button className="w-8 h-8 rounded flex items-center justify-center hover:bg-surface-container transition-colors">2</button>
            <button className="w-8 h-8 rounded flex items-center justify-center hover:bg-surface-container transition-colors">3</button>
            <span className="px-1">...</span>
            <button className="w-8 h-8 rounded flex items-center justify-center hover:bg-surface-container transition-colors">8</button>
            <button className="w-8 h-8 rounded flex items-center justify-center hover:bg-surface-container transition-colors">
              <span className="material-symbols-outlined text-[20px]">chevron_right</span>
            </button>
          </div>
        </div>
      </div>
    </main>
    </>
  );
}
