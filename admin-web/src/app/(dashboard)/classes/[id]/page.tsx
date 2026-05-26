"use client";

import React, { useState } from "react";
import Link from "next/link";

export default function ClassDetailPage({ params }: { params: { id: string } }) {
  const students = [
    { stt: 1, mssv: "SV2023001", name: "Trần Văn Hoàng", present: 12, absent: 0, percent: "100%", status: "Good" },
    { stt: 2, mssv: "SV2023045", name: "Nguyễn Thị Mai", present: 10, absent: 2, percent: "83%", status: "Normal" },
    { stt: 3, mssv: "SV2023089", name: "Lê Đình Minh", present: 8, absent: 4, percent: "66%", status: "Warning" },
    { stt: 4, mssv: "SV2023102", name: "Phạm Thu Trang", present: 11, absent: 1, percent: "91%", status: "Normal" },
  ];

  const [showNotificationModal, setShowNotificationModal] = useState(false);
  const [showGradeModal, setShowGradeModal] = useState(false);
  const [notificationForm, setNotificationForm] = useState({ title: "", content: "", channel: "both" });
  const [gradeFile, setGradeFile] = useState<File | null>(null);

  const handleSendNotification = (e: React.FormEvent) => {
    e.preventDefault();
    alert("Đã gửi thông báo thành công!");
    setShowNotificationModal(false);
    setNotificationForm({ title: "", content: "", channel: "both" });
  };

  const handleUploadGrades = (e: React.FormEvent) => {
    e.preventDefault();
    if (!gradeFile) {
      alert("Vui lòng chọn file Excel bảng điểm.");
      return;
    }
    alert("Đã cập nhật điểm thành công từ file " + gradeFile.name);
    setShowGradeModal(false);
    setGradeFile(null);
  };

  return (
    <main className="flex-1 pt-20 px-xl pb-xl bg-background overflow-y-auto">
      {/* Back Navigation */}
      <Link 
        href="/classes" 
        className="inline-flex items-center gap-sm text-on-surface-variant hover:text-primary transition-colors duration-200 mb-lg font-button text-button"
      >
        <span className="material-symbols-outlined text-[20px]">arrow_back</span>
        Quay lại danh sách lớp
      </Link>

      {/* Header & Actions */}
      <div className="flex flex-col md:flex-row justify-between items-start md:items-end mb-xl gap-4">
        <div>
          <p className="font-label text-label text-primary mb-xs uppercase tracking-wider">CS101</p>
          <h2 className="font-h1 text-h1 text-on-surface">Nhập môn Khoa học Máy tính</h2>
          <p className="font-body-md text-body-md text-on-surface-variant mt-1">Giảng viên: PGS. TS. Nguyễn Văn A • Học kỳ 1, 2023-2024</p>
        </div>
        <div className="flex gap-md w-full md:w-auto">
          <button onClick={() => setShowNotificationModal(true)} className="flex-1 md:flex-none bg-surface-container-highest text-on-surface font-button text-button px-4 py-2 rounded-lg hover:bg-surface-dim transition-colors duration-200 flex items-center justify-center gap-sm">
            <span className="material-symbols-outlined text-[20px]">campaign</span>
            Gửi thông báo lớp
          </button>
          <button onClick={() => setShowGradeModal(true)} className="flex-1 md:flex-none bg-[#6C63FF] text-white font-button text-button px-4 py-2 rounded-lg hover:bg-[#554cb9] transition-colors duration-200 flex items-center justify-center gap-sm shadow-[0px_4px_12px_rgba(108,99,255,0.25)]">
            <span className="material-symbols-outlined text-[20px]">edit_document</span>
            Nhập điểm
          </button>
        </div>
      </div>

      {/* Stats Overview */}
      <div className="grid grid-cols-1 md:grid-cols-3 gap-container_gutter mb-xl">
        <div className="bg-surface-container-lowest p-lg rounded-xl shadow-[0px_4px_20px_rgba(26,26,26,0.05)] border border-outline-variant/20 flex flex-col">
          <div className="flex items-center gap-sm mb-md text-on-surface-variant">
            <span className="material-symbols-outlined p-2 bg-primary-container/20 text-primary rounded-lg">groups</span>
            <span className="font-label text-label uppercase">Sĩ số lớp</span>
          </div>
          <div className="flex items-baseline gap-2">
            <span className="font-h1 text-h1 text-on-surface">45</span>
            <span className="font-body-sm text-body-sm text-on-surface-variant">sinh viên</span>
          </div>
        </div>
        <div className="bg-surface-container-lowest p-lg rounded-xl shadow-[0px_4px_20px_rgba(26,26,26,0.05)] border border-outline-variant/20 flex flex-col">
          <div className="flex items-center gap-sm mb-md text-on-surface-variant">
            <span className="material-symbols-outlined p-2 bg-tertiary-container/20 text-tertiary rounded-lg">calendar_month</span>
            <span className="font-label text-label uppercase">Số buổi đã dạy</span>
          </div>
          <div className="flex items-baseline gap-2">
            <span className="font-h1 text-h1 text-on-surface">12</span>
            <span className="font-body-lg text-body-lg text-on-surface-variant">/ 15</span>
            <span className="font-body-sm text-body-sm text-on-surface-variant ml-1">buổi</span>
          </div>
          <div className="w-full bg-surface-container-highest h-1.5 rounded-full mt-4 overflow-hidden">
            <div className="bg-tertiary h-full w-[80%] rounded-full"></div>
          </div>
        </div>
        <div className="bg-surface-container-lowest p-lg rounded-xl shadow-[0px_4px_20px_rgba(26,26,26,0.05)] border border-outline-variant/20 flex flex-col">
          <div className="flex items-center gap-sm mb-md text-on-surface-variant">
            <span className="material-symbols-outlined p-2 bg-secondary-container/40 text-secondary rounded-lg">fact_check</span>
            <span className="font-label text-label uppercase">Tỷ lệ chuyên cần TB</span>
          </div>
          <div className="flex items-baseline gap-2">
            <span className="font-h1 text-h1 text-on-surface">94</span>
            <span className="font-h2 text-h2 text-on-surface">%</span>
          </div>
          <p className="font-body-sm text-body-sm text-secondary mt-2 flex items-center gap-1">
            <span className="material-symbols-outlined text-[16px]">trending_up</span>
            Tốt
          </p>
        </div>
      </div>

      {/* Student List Container */}
      <div className="bg-surface-container-lowest rounded-xl shadow-[0px_4px_20px_rgba(26,26,26,0.05)] border border-outline-variant/20 overflow-hidden flex flex-col">
        <div className="p-lg border-b border-outline-variant/20 flex flex-col sm:flex-row justify-between items-start sm:items-center gap-4">
          <h3 className="font-h3 text-h3 text-on-surface">Danh sách sinh viên</h3>
          <div className="relative w-full sm:w-64">
            <span className="material-symbols-outlined absolute left-3 top-1/2 -translate-y-1/2 text-outline text-[20px]">search</span>
            <input 
              className="w-full pl-10 pr-4 py-2 border border-outline-variant rounded-lg focus:outline-none focus:border-primary focus:ring-1 focus:ring-primary font-body-sm text-body-sm bg-surface-bright" 
              placeholder="Tìm theo tên, MSSV..." 
              type="text"
            />
          </div>
        </div>
        {/* Table */}
        <div className="overflow-x-auto">
          <table className="w-full text-left border-collapse">
            <thead>
              <tr className="bg-surface-container-low border-b border-outline-variant/20">
                <th className="py-4 px-6 font-label text-label text-on-surface-variant uppercase w-16">STT</th>
                <th className="py-4 px-6 font-label text-label text-on-surface-variant uppercase">MSSV</th>
                <th className="py-4 px-6 font-label text-label text-on-surface-variant uppercase">Họ và tên</th>
                <th className="py-4 px-6 font-label text-label text-on-surface-variant uppercase text-center">Có mặt</th>
                <th className="py-4 px-6 font-label text-label text-on-surface-variant uppercase text-center">Vắng</th>
                <th className="py-4 px-6 font-label text-label text-on-surface-variant uppercase text-center">Điểm danh</th>
                <th className="py-4 px-6 font-label text-label text-on-surface-variant uppercase text-right">Hành động</th>
              </tr>
            </thead>
            <tbody className="font-body-md text-body-md text-on-surface divide-y divide-outline-variant/10">
              {students.map((s) => (
                <tr key={s.stt} className="hover:bg-surface-container-low transition-colors duration-150">
                  <td className="py-4 px-6 text-on-surface-variant">{s.stt}</td>
                  <td className="py-4 px-6 font-medium">{s.mssv}</td>
                  <td className="py-4 px-6">{s.name}</td>
                  <td className="py-4 px-6 text-center font-medium text-secondary">{s.present}</td>
                  <td className="py-4 px-6 text-center text-error font-medium">{s.absent}</td>
                  <td className="py-4 px-6 text-center">
                    <span className={`inline-flex items-center px-2 py-1 rounded-full font-label text-[10px] uppercase ${
                      s.status === 'Good' ? 'bg-secondary-container/30 text-secondary' : 
                      s.status === 'Warning' ? 'bg-error-container/50 text-error' : 
                      'bg-surface-container-highest text-on-surface-variant'
                    }`}>
                      {s.percent}
                    </span>
                  </td>
                  <td className="py-4 px-6 text-right">
                    <button className="text-outline hover:text-primary transition-colors p-1">
                      <span className="material-symbols-outlined text-[20px]">visibility</span>
                    </button>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
        {/* Pagination Footer */}
        <div className="p-md border-t border-outline-variant/20 flex justify-between items-center bg-surface-bright">
          <span className="font-body-sm text-body-sm text-on-surface-variant">Hiển thị 1-4 trong 45 sinh viên</span>
          <div className="flex gap-2">
            <button className="w-8 h-8 flex items-center justify-center rounded-md border border-outline-variant/50 text-outline hover:bg-surface-container-low transition-colors disabled:opacity-50" disabled>
              <span className="material-symbols-outlined text-[18px]">chevron_left</span>
            </button>
            <button className="w-8 h-8 flex items-center justify-center rounded-md bg-primary text-white font-button text-[12px]">1</button>
            <button className="w-8 h-8 flex items-center justify-center rounded-md border border-outline-variant/50 text-on-surface hover:bg-surface-container-low transition-colors font-button text-[12px]">2</button>
            <button className="w-8 h-8 flex items-center justify-center rounded-md border border-outline-variant/50 text-on-surface hover:bg-surface-container-low transition-colors font-button text-[12px]">3</button>
            <span className="w-8 h-8 flex items-center justify-center text-outline">...</span>
            <button className="w-8 h-8 flex items-center justify-center rounded-md border border-outline-variant/50 text-outline hover:bg-surface-container-low transition-colors">
              <span className="material-symbols-outlined text-[18px]">chevron_right</span>
            </button>
          </div>
        </div>
      </div>

      {/* Notification Modal */}
      {showNotificationModal && (
        <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/40 backdrop-blur-sm p-4">
          <div className="bg-surface-container-lowest rounded-xl shadow-[var(--shadow-float)] border border-border-muted p-xl w-full max-w-2xl">
            <h3 className="text-h3 text-on-surface mb-md">Gửi thông báo cho lớp CS101</h3>
            <form className="space-y-4" onSubmit={handleSendNotification}>
              <div>
                <label className="block text-label font-label text-on-surface-variant mb-1">Tiêu đề thông báo <span className="text-error">*</span></label>
                <input type="text" value={notificationForm.title} onChange={e => setNotificationForm({...notificationForm, title: e.target.value})} className="w-full bg-surface border border-outline-variant rounded-lg px-3 py-2 text-body-md focus:border-primary outline-none" required placeholder="VD: Thay đổi lịch học tuần tới" />
              </div>
              <div>
                <label className="block text-label font-label text-on-surface-variant mb-1">Nội dung <span className="text-error">*</span></label>
                <textarea rows={5} value={notificationForm.content} onChange={e => setNotificationForm({...notificationForm, content: e.target.value})} className="w-full bg-surface border border-outline-variant rounded-lg px-3 py-2 text-body-md focus:border-primary outline-none resize-none" required placeholder="Nhập nội dung thông báo..."></textarea>
              </div>
              <div>
                <label className="block text-label font-label text-on-surface-variant mb-2">Kênh gửi</label>
                <div className="flex gap-4">
                  <label className="flex items-center gap-2 cursor-pointer">
                    <input type="radio" name="channel" value="in-app" checked={notificationForm.channel === 'in-app'} onChange={e => setNotificationForm({...notificationForm, channel: e.target.value})} className="w-4 h-4 text-primary focus:ring-primary border-outline-variant" />
                    <span className="text-body-md text-on-surface">Chỉ thông báo hệ thống</span>
                  </label>
                  <label className="flex items-center gap-2 cursor-pointer">
                    <input type="radio" name="channel" value="both" checked={notificationForm.channel === 'both'} onChange={e => setNotificationForm({...notificationForm, channel: e.target.value})} className="w-4 h-4 text-primary focus:ring-primary border-outline-variant" />
                    <span className="text-body-md text-on-surface">Hệ thống & Gửi Email</span>
                  </label>
                </div>
              </div>
              <div className="flex gap-3 justify-end pt-4 border-t border-outline-variant/30">
                <button type="button" onClick={() => setShowNotificationModal(false)} className="px-4 py-2 rounded-lg border border-outline-variant hover:bg-surface-container transition-colors font-button text-button">Hủy</button>
                <button type="submit" className="px-4 py-2 rounded-lg bg-primary text-on-primary hover:bg-primary/90 transition-colors font-button text-button flex items-center gap-2">
                  <span className="material-symbols-outlined text-[18px]">send</span>
                  Gửi thông báo
                </button>
              </div>
            </form>
          </div>
        </div>
      )}

      {/* Grade Import Modal */}
      {showGradeModal && (
        <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/40 backdrop-blur-sm p-4">
          <div className="bg-surface-container-lowest rounded-xl shadow-[var(--shadow-float)] border border-border-muted p-xl w-full max-w-3xl">
            <h3 className="text-h3 text-on-surface mb-md">Nhập điểm lớp CS101</h3>
            <form onSubmit={handleUploadGrades}>
              <div className="mb-6 flex gap-4">
                <button type="button" className="flex-1 py-4 border-2 border-primary bg-primary-container/10 rounded-xl flex flex-col items-center justify-center text-primary gap-2 transition-colors">
                  <span className="material-symbols-outlined text-[32px]">upload_file</span>
                  <span className="font-semibold text-body-md">Tải lên file Excel</span>
                </button>
                <button type="button" className="flex-1 py-4 border-2 border-outline-variant rounded-xl flex flex-col items-center justify-center text-on-surface-variant hover:border-primary hover:text-primary transition-colors">
                  <span className="material-symbols-outlined text-[32px]">edit_square</span>
                  <span className="font-semibold text-body-md">Nhập điểm thủ công</span>
                </button>
              </div>
              
              <div className="mb-4">
                <div 
                  className={`border-2 border-dashed rounded-xl p-8 flex flex-col items-center justify-center text-center transition-colors ${gradeFile ? "border-primary bg-primary-container/10" : "border-outline-variant hover:border-primary hover:bg-surface-container-low"}`}
                >
                  <span className={`material-symbols-outlined text-[48px] mb-4 ${gradeFile ? "text-primary" : "text-outline"}`}>
                    {gradeFile ? "description" : "cloud_upload"}
                  </span>
                  {gradeFile ? (
                    <div>
                      <p className="text-body-lg font-semibold text-on-surface mb-1">{gradeFile.name}</p>
                      <p className="text-body-sm text-on-surface-variant">{(gradeFile.size / 1024).toFixed(1)} KB</p>
                    </div>
                  ) : (
                    <div>
                      <p className="text-body-lg text-on-surface font-semibold mb-1">Kéo thả file Excel điểm vào đây</p>
                      <p className="text-body-md text-on-surface-variant mb-4">hoặc</p>
                      <label className="bg-surface-container-high hover:bg-surface-dim text-on-surface px-4 py-2 rounded-lg font-button cursor-pointer transition-colors inline-block border border-outline-variant">
                        Chọn file từ máy
                        <input type="file" accept=".csv, application/vnd.openxmlformats-officedocument.spreadsheetml.sheet, application/vnd.ms-excel" className="hidden" onChange={(e) => e.target.files && setGradeFile(e.target.files[0])} />
                      </label>
                    </div>
                  )}
                </div>
                <div className="mt-3 text-right">
                  <a href="#" className="text-body-sm text-primary hover:underline">Tải file mẫu định dạng điểm (.xlsx)</a>
                </div>
              </div>

              <div className="flex gap-3 justify-end pt-4 border-t border-outline-variant/30">
                <button type="button" onClick={() => { setShowGradeModal(false); setGradeFile(null); }} className="px-4 py-2 rounded-lg border border-outline-variant hover:bg-surface-container transition-colors font-button text-button">Hủy</button>
                <button type="submit" disabled={!gradeFile} className="px-4 py-2 rounded-lg bg-[#6C63FF] text-white hover:bg-[#554cb9] transition-colors font-button text-button disabled:opacity-50">Cập nhật điểm</button>
              </div>
            </form>
          </div>
        </div>
      )}
    </main>
  );
}
