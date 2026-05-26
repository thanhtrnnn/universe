"use client";

import React, { useState } from "react";

interface Schedule {
  id: string;
  classId: string;
  className: string;
  courseName: string;
  lecturerName: string;
  roomId: string;
  roomName: string;
  dayOfWeek: number; // 2-8 (Thứ 2 - Chủ Nhật)
  startPeriod: number; // 1-12
  endPeriod: number;
}

const mockSchedules: Schedule[] = [
  { id: "1", classId: "c1", className: "IT101-01", courseName: "Nhập môn Lập trình", lecturerName: "Trần Văn A", roomId: "r1", roomName: "P.301", dayOfWeek: 2, startPeriod: 1, endPeriod: 3 },
  { id: "2", classId: "c2", className: "IT202-02", courseName: "Cấu trúc dữ liệu", lecturerName: "Nguyễn Thị B", roomId: "r2", roomName: "P.302", dayOfWeek: 3, startPeriod: 4, endPeriod: 6 },
  { id: "3", classId: "c3", className: "BA101-01", courseName: "Kinh tế vi mô", lecturerName: "Lê Văn C", roomId: "r1", roomName: "P.301", dayOfWeek: 2, startPeriod: 4, endPeriod: 6 },
];

export default function SchedulesPage() {
  const [schedules, setSchedules] = useState<Schedule[]>(mockSchedules);
  const [showAddModal, setShowAddModal] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const [showImportModal, setShowImportModal] = useState(false);
  const [importFile, setImportFile] = useState<File | null>(null);

  // Add form state
  const [form, setForm] = useState({
    className: "",
    courseName: "",
    lecturerName: "",
    roomName: "",
    dayOfWeek: "2",
    startPeriod: "1",
    endPeriod: "3"
  });

  const checkConflict = (newSchedule: any) => {
    return schedules.find(s => 
      s.dayOfWeek === parseInt(newSchedule.dayOfWeek) &&
      (
        (parseInt(newSchedule.startPeriod) >= s.startPeriod && parseInt(newSchedule.startPeriod) <= s.endPeriod) ||
        (parseInt(newSchedule.endPeriod) >= s.startPeriod && parseInt(newSchedule.endPeriod) <= s.endPeriod)
      ) &&
      (s.roomName === newSchedule.roomName || s.lecturerName === newSchedule.lecturerName)
    );
  };

  const handleAddSchedule = (e: React.FormEvent) => {
    e.preventDefault();
    setError(null);
    
    const conflict = checkConflict(form);
    if (conflict) {
      if (conflict.roomName === form.roomName) {
        setError(`Xung đột phòng học: Phòng ${conflict.roomName} đã có lớp ${conflict.className} vào Thứ ${conflict.dayOfWeek}, Tiết ${conflict.startPeriod}-${conflict.endPeriod}.`);
      } else {
        setError(`Xung đột giảng viên: GV ${conflict.lecturerName} đã có lịch dạy lớp ${conflict.className} vào Thứ ${conflict.dayOfWeek}, Tiết ${conflict.startPeriod}-${conflict.endPeriod}.`);
      }
      return;
    }

    setSchedules([...schedules, {
      id: Math.random().toString(),
      classId: "cx",
      roomId: "rx",
      ...form,
      dayOfWeek: parseInt(form.dayOfWeek),
      startPeriod: parseInt(form.startPeriod),
      endPeriod: parseInt(form.endPeriod),
    }]);
    setShowAddModal(false);
  };

  const handleImport = (e: React.FormEvent) => {
    e.preventDefault();
    if (!importFile) {
      alert("Vui lòng chọn file Excel thời khóa biểu.");
      return;
    }
    alert("Đã nhập thời khóa biểu thành công từ file " + importFile.name);
    setShowImportModal(false);
    setImportFile(null);
  };

  const days = ["Thứ 2", "Thứ 3", "Thứ 4", "Thứ 5", "Thứ 6", "Thứ 7", "Chủ nhật"];
  const periods = [1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12];

  const getPeriodTime = (p: number) => {
    const startHour = 6 + p;
    const endHour = 7 + p;
    const formatTime = (h: number) => `${h < 10 ? '0' : ''}${h}:00`;
    return `${formatTime(startHour)} - ${formatTime(endHour)}`;
  };

  return (
    <main className="flex-1 overflow-y-auto mt-16 p-xl bg-background">
      {/* Header */}
      <div className="flex justify-between items-end mb-lg">
        <div>
          <h2 className="text-h1 text-on-surface">Thời khóa biểu</h2>
          <p className="text-body-md text-on-surface-variant mt-1">
            Quản lý và sắp xếp lịch học, tự động phát hiện xung đột
          </p>
        </div>
        <div className="flex gap-2">
          <button 
            onClick={() => setShowImportModal(true)}
            className="bg-surface-container-highest hover:bg-surface-dim text-on-surface text-button px-4 py-2 rounded-lg flex items-center gap-2 shadow-sm transition-colors duration-200"
          >
            <span className="material-symbols-outlined text-[20px]">upload_file</span>
            Nhập từ Excel
          </button>
          <button 
            onClick={() => setShowAddModal(true)}
            className="bg-primary hover:bg-surface-tint text-on-primary text-button px-4 py-2 rounded-lg flex items-center gap-2 shadow-sm transition-colors duration-200"
          >
            <span className="material-symbols-outlined text-[20px]">calendar_add_on</span>
            Xếp lịch học
          </button>
        </div>
      </div>

      {/* Calendar Grid */}
      <div className="bg-surface-container-lowest rounded-xl shadow-[var(--shadow-card)] border border-border-muted overflow-hidden">
        <div className="grid grid-cols-8 border-b border-border-muted bg-surface">
          <div className="p-3 text-center border-r border-border-muted font-label text-label text-on-surface-variant">Ca / Thứ</div>
          {days.map((day, i) => (
            <div key={day} className={`p-3 text-center font-label text-label text-on-surface-variant ${i < days.length - 1 ? 'border-r border-border-muted' : ''}`}>
              {day}
            </div>
          ))}
        </div>
        
        <div className="flex flex-col">
          {periods.map(period => (
            <div key={period} className="grid grid-cols-8 border-b border-border-muted last:border-b-0">
              <div className="p-3 text-center border-r border-border-muted bg-surface font-body-sm text-on-surface-variant flex flex-col items-center justify-center">
                <span className="font-semibold text-on-surface">Tiết {period}</span>
                <span className="text-[10px] opacity-80 mt-0.5">{getPeriodTime(period)}</span>
              </div>
              {days.map((_, dayIndex) => {
                const dayOfWeek = dayIndex + 2;
                const schedule = schedules.find(s => s.dayOfWeek === dayOfWeek && s.startPeriod <= period && s.endPeriod >= period);
                const isStart = schedule && schedule.startPeriod === period;
                
                return (
                  <div key={dayIndex} className={`p-1 ${dayIndex < days.length - 1 ? 'border-r border-border-muted' : ''} ${schedule ? 'bg-primary-container/20' : ''}`}>
                    {isStart && schedule && (
                      <div className="bg-primary-container border border-primary/20 rounded-md p-2 shadow-sm">
                        <div className="font-semibold text-on-primary-container text-body-sm">{schedule.className}</div>
                        <div className="text-xs text-on-primary-container/80 truncate" title={schedule.courseName}>{schedule.courseName}</div>
                        <div className="text-xs text-on-primary-container/80 mt-1 flex items-center gap-1">
                          <span className="material-symbols-outlined text-[14px]">meeting_room</span> {schedule.roomName}
                        </div>
                        <div className="text-xs text-on-primary-container/80 flex items-center gap-1">
                          <span className="material-symbols-outlined text-[14px]">person</span> {schedule.lecturerName}
                        </div>
                      </div>
                    )}
                  </div>
                );
              })}
            </div>
          ))}
        </div>
      </div>

      {/* Add Schedule Modal */}
      {showAddModal && (
        <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/40 backdrop-blur-sm p-4 overflow-y-auto">
          <div className="bg-surface-container-lowest rounded-xl shadow-[var(--shadow-float)] border border-border-muted p-xl w-full max-w-3xl my-8">
            <h3 className="text-h3 text-on-surface mb-md">Xếp lịch học mới</h3>
            {error && (
              <div className="mb-4 p-3 bg-error-container text-on-error-container rounded-lg flex gap-2 items-start text-body-sm">
                <span className="material-symbols-outlined text-[18px]">warning</span>
                <span>{error}</span>
              </div>
            )}
            <form className="space-y-4" onSubmit={handleAddSchedule}>
              <div className="grid grid-cols-2 gap-4">
                <div>
                  <label className="block text-label font-label text-on-surface-variant mb-1">Mã Lớp học</label>
                  <input type="text" value={form.className} onChange={e => setForm({...form, className: e.target.value})} className="w-full bg-surface border border-outline-variant rounded-lg px-3 py-2 focus:border-primary outline-none" required />
                </div>
                <div>
                  <label className="block text-label font-label text-on-surface-variant mb-1">Tên môn học</label>
                  <input type="text" value={form.courseName} onChange={e => setForm({...form, courseName: e.target.value})} className="w-full bg-surface border border-outline-variant rounded-lg px-3 py-2 focus:border-primary outline-none" required />
                </div>
              </div>
              <div className="grid grid-cols-2 gap-4">
                <div>
                  <label className="block text-label font-label text-on-surface-variant mb-1">Giảng viên</label>
                  <input type="text" value={form.lecturerName} onChange={e => setForm({...form, lecturerName: e.target.value})} className="w-full bg-surface border border-outline-variant rounded-lg px-3 py-2 focus:border-primary outline-none" required />
                </div>
                <div>
                  <label className="block text-label font-label text-on-surface-variant mb-1">Phòng học</label>
                  <input type="text" value={form.roomName} onChange={e => setForm({...form, roomName: e.target.value})} className="w-full bg-surface border border-outline-variant rounded-lg px-3 py-2 focus:border-primary outline-none" required />
                </div>
              </div>
              <div className="grid grid-cols-3 gap-4">
                <div>
                  <label className="block text-label font-label text-on-surface-variant mb-1">Thứ</label>
                  <select value={form.dayOfWeek} onChange={e => setForm({...form, dayOfWeek: e.target.value})} className="w-full bg-surface border border-outline-variant rounded-lg px-3 py-2 focus:border-primary outline-none">
                    {days.map((d, i) => <option key={i} value={i + 2}>{d}</option>)}
                  </select>
                </div>
                <div>
                  <label className="block text-label font-label text-on-surface-variant mb-1">Từ tiết</label>
                  <select value={form.startPeriod} onChange={e => setForm({...form, startPeriod: e.target.value})} className="w-full bg-surface border border-outline-variant rounded-lg px-3 py-2 focus:border-primary outline-none">
                    {periods.map(p => <option key={p} value={p}>{p}</option>)}
                  </select>
                </div>
                <div>
                  <label className="block text-label font-label text-on-surface-variant mb-1">Đến tiết</label>
                  <select value={form.endPeriod} onChange={e => setForm({...form, endPeriod: e.target.value})} className="w-full bg-surface border border-outline-variant rounded-lg px-3 py-2 focus:border-primary outline-none">
                    {periods.map(p => <option key={p} value={p}>{p}</option>)}
                  </select>
                </div>
              </div>
              <div className="flex gap-3 justify-end pt-4 border-t border-outline-variant/30">
                <button type="button" onClick={() => {setShowAddModal(false); setError(null);}} className="px-4 py-2 rounded-lg border border-outline-variant hover:bg-surface-container transition-colors">Hủy</button>
                <button type="submit" className="px-4 py-2 rounded-lg bg-primary text-on-primary hover:bg-primary/90 transition-colors">Lưu lịch học</button>
              </div>
            </form>
          </div>
        </div>
      )}

      {/* Import Modal */}
      {showImportModal && (
        <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/40 backdrop-blur-sm p-4 overflow-y-auto">
          <div className="bg-surface-container-lowest rounded-xl shadow-[var(--shadow-float)] border border-border-muted p-xl w-full max-w-3xl my-8">
            <h3 className="text-h3 text-on-surface mb-md">Nhập thời khóa biểu từ Excel</h3>
            <form onSubmit={handleImport}>
              <div className="mb-4">
                <div 
                  className={`border-2 border-dashed rounded-xl p-8 flex flex-col items-center justify-center text-center transition-colors ${importFile ? "border-primary bg-primary-container/10" : "border-outline-variant hover:border-primary hover:bg-surface-container-low"}`}
                >
                  <span className={`material-symbols-outlined text-[48px] mb-4 ${importFile ? "text-primary" : "text-outline"}`}>
                    {importFile ? "description" : "cloud_upload"}
                  </span>
                  {importFile ? (
                    <div>
                      <p className="text-body-lg font-semibold text-on-surface mb-1">{importFile.name}</p>
                      <p className="text-body-sm text-on-surface-variant">{(importFile.size / 1024).toFixed(1)} KB</p>
                    </div>
                  ) : (
                    <div>
                      <p className="text-body-lg text-on-surface font-semibold mb-1">Kéo thả file Excel TKB vào đây</p>
                      <p className="text-body-md text-on-surface-variant mb-4">hoặc</p>
                      <label className="bg-surface-container-high hover:bg-surface-dim text-on-surface px-4 py-2 rounded-lg font-button cursor-pointer transition-colors inline-block border border-outline-variant">
                        Chọn file từ máy
                        <input type="file" accept=".csv, application/vnd.openxmlformats-officedocument.spreadsheetml.sheet, application/vnd.ms-excel" className="hidden" onChange={(e) => e.target.files && setImportFile(e.target.files[0])} />
                      </label>
                    </div>
                  )}
                </div>
                <div className="mt-3 text-right">
                  <a href="#" className="text-body-sm text-primary hover:underline">Tải file mẫu định dạng TKB (.xlsx)</a>
                </div>
              </div>

              <div className="flex gap-3 justify-end pt-4 border-t border-outline-variant/30">
                <button type="button" onClick={() => { setShowImportModal(false); setImportFile(null); }} className="px-4 py-2 rounded-lg border border-outline-variant hover:bg-surface-container transition-colors font-button text-button">Hủy</button>
                <button type="submit" disabled={!importFile} className="px-4 py-2 rounded-lg bg-primary text-on-primary hover:bg-primary/90 transition-colors font-button text-button disabled:opacity-50">Nhập dữ liệu</button>
              </div>
            </form>
          </div>
        </div>
      )}
    </main>
  );
}
