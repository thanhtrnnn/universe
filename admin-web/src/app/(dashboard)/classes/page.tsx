"use client";

import React, { useState } from "react";
import Link from "next/link";

export default function ClassesPage() {
  const classes = [
    { id: "CS101.A1", name: "Nhập môn Lập trình", teacher: "Nguyễn Văn A", initials: "NV", initialsColor: "bg-primary-container/20 text-primary", room: "Phòng 302, Tòa A", size: "45/50", status: "Đang học", statusType: "success" },
    { id: "DB202.B2", name: "Cơ sở dữ liệu", teacher: "Trần Thị B", initials: "TT", initialsColor: "bg-tertiary-container/20 text-tertiary", room: "Phòng 105, Tòa C", size: "38/40", status: "Đang học", statusType: "success" },
    { id: "MA100.C1", name: "Toán cao cấp 1", teacher: "Lê Hoàng C", initials: "LH", initialsColor: "bg-error-container/30 text-error", room: "Phòng 401, Tòa B", size: "60/60", status: "Kết thúc", statusType: "neutral" },
    { id: "EN101.D4", name: "Tiếng Anh giao tiếp", teacher: "Phạm Minh D", initials: "PM", initialsColor: "bg-secondary-container/30 text-secondary", room: "Phòng 208, Tòa D", size: "25/30", status: "Đang học", statusType: "success" },
  ];

  const getStatusStyle = (type: string) => {
    switch (type) {
      case "success": return "bg-secondary-container text-on-secondary-container";
      case "neutral": return "bg-surface-container-high text-on-surface-variant";
      default: return "bg-surface-container-high text-on-surface-variant";
    }
  };

  const [classList, setClassList] = useState(classes);
  const [showAddModal, setShowAddModal] = useState(false);
  const [form, setForm] = useState({ id: "", name: "", teacher: "", room: "", size: "" });

  const handleAdd = (e: React.FormEvent) => {
    e.preventDefault();
    setClassList([{
      id: form.id,
      name: form.name,
      teacher: form.teacher,
      initials: form.teacher.split(" ").slice-1?.join("")[0] ?? "GV",
      initialsColor: "bg-primary-container/20 text-primary",
      room: form.room,
      size: form.size,
      status: "Đang học",
      statusType: "success"
    }, ...classList]);
    setShowAddModal(false);
    setForm({ id: "", name: "", teacher: "", room: "", size: "" });
  };

  return (
    <main className="flex-1 overflow-y-auto mt-16 p-xl bg-background">
      {/* Page Header */}
      <div className="flex justify-between items-end mb-lg">
        <div>
          <h2 className="text-h1 text-on-surface">Quản lý Lớp học</h2>
          <p className="text-body-md text-on-surface-variant mt-1">
            Xem, tìm kiếm và tạo mới các lớp học trong hệ thống.
          </p>
        </div>
        <button onClick={() => setShowAddModal(true)} className="bg-primary hover:bg-surface-tint text-on-primary text-button px-4 py-2 rounded-lg flex items-center gap-2 shadow-sm transition-colors duration-200">
          <span className="material-symbols-outlined text-[20px]">add</span>
          Mở lớp mới
        </button>
      </div>

      {/* Filters */}
      <div className="flex flex-wrap gap-3 mb-lg items-center">
        <div className="relative flex-1 max-w-md">
          <span className="material-symbols-outlined absolute left-3 top-1/2 -translate-y-1/2 text-on-surface-variant">search</span>
          <input
            className="w-full pl-10 pr-4 py-2 bg-surface-container-lowest border border-outline-variant rounded-lg focus:border-primary focus:ring-1 focus:ring-primary outline-none transition-all text-body-md placeholder:text-on-surface-variant/70"
            placeholder="Tìm theo mã lớp, tên môn..."
            type="text"
          />
        </div>
        <div className="relative">
          <select className="bg-surface-container-lowest border border-outline-variant rounded-lg px-4 py-2 text-body-md appearance-none pr-10 focus:outline-none focus:border-primary focus:ring-1 focus:ring-primary transition-colors">
            <option>Học kỳ</option>
            <option>HK1 2024-2025</option>
            <option>HK2 2023-2024</option>
          </select>
          <span className="material-symbols-outlined absolute right-3 top-1/2 -translate-y-1/2 text-outline-variant pointer-events-none text-[18px]">expand_more</span>
        </div>
        <div className="relative">
          <select className="bg-surface-container-lowest border border-outline-variant rounded-lg px-4 py-2 text-body-md appearance-none pr-10 focus:outline-none focus:border-primary focus:ring-1 focus:ring-primary transition-colors">
            <option>Khoa / Bộ môn</option>
            <option>CNTT</option>
            <option>Kinh tế</option>
          </select>
          <span className="material-symbols-outlined absolute right-3 top-1/2 -translate-y-1/2 text-outline-variant pointer-events-none text-[18px]">expand_more</span>
        </div>
        <button className="p-2 border border-outline-variant rounded-lg hover:bg-surface-container transition-colors">
          <span className="material-symbols-outlined text-on-surface-variant">tune</span>
        </button>
      </div>

      {/* Table */}
      <div className="bg-surface-container-lowest rounded-xl shadow-[var(--shadow-card)] border border-border-muted overflow-hidden">
        <div className="overflow-x-auto">
          <table className="w-full text-left border-collapse">
            <thead className="bg-surface-container-low border-b border-border-muted">
              <tr>
                <th className="py-3 px-4 text-label text-on-surface-variant uppercase tracking-wider">MÃ LỚP</th>
                <th className="py-3 px-4 text-label text-on-surface-variant uppercase tracking-wider">TÊN MÔN HỌC</th>
                <th className="py-3 px-4 text-label text-on-surface-variant uppercase tracking-wider">GIẢNG VIÊN PHỤ TRÁCH</th>
                <th className="py-3 px-4 text-label text-on-surface-variant uppercase tracking-wider">PHÒNG HỌC</th>
                <th className="py-3 px-4 text-label text-on-surface-variant uppercase tracking-wider">SĨ SỐ</th>
                <th className="py-3 px-4 text-label text-on-surface-variant uppercase tracking-wider">TRẠNG THÁI</th>
                <th className="py-3 px-4 text-label text-on-surface-variant uppercase tracking-wider text-center">THAO TÁC</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-outline-variant/20 text-body-md text-on-surface">
              {classList.map((cls, idx) => (
                <tr key={idx} className="hover:bg-surface-container-lowest/50 transition-colors">
                  <td className="py-4 px-4">
                    <Link href={`/classes/${cls.id}`} className="text-button text-primary hover:underline">{cls.id}</Link>
                  </td>
                  <td className="py-4 px-4">
                    <Link href={`/classes/${cls.id}`} className="hover:text-primary transition-colors">{cls.name}</Link>
                  </td>
                  <td className="py-4 px-4">
                    <div className="flex items-center gap-2">
                      <div className={`w-8 h-8 rounded-full ${cls.initialsColor} flex items-center justify-center text-label`}>{cls.initials}</div>
                      {cls.teacher}
                    </div>
                  </td>
                  <td className="py-4 px-4 text-on-surface-variant">{cls.room}</td>
                  <td className="py-4 px-4">{cls.size}</td>
                  <td className="py-4 px-4">
                    <span className={`inline-flex items-center px-2 py-1 rounded-full text-label text-[10px] ${getStatusStyle(cls.statusType)}`}>{cls.status}</span>
                  </td>
                  <td className="py-4 px-4 text-center">
                    <Link href={`/classes/${cls.id}`} className="text-on-surface-variant hover:text-primary p-1 transition-colors inline-block" title="Xem chi tiết">
                      <span className="material-symbols-outlined text-[18px]">visibility</span>
                    </Link>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>

        {/* Pagination */}
        <div className="p-md border-t border-border-muted bg-surface flex justify-between items-center">
          <p className="text-body-sm text-on-surface-variant">Hiển thị 1-4 trong số 120 lớp</p>
          <div className="flex items-center gap-1">
            <button className="w-8 h-8 rounded flex items-center justify-center border border-outline-variant bg-surface-container-lowest text-on-surface-variant hover:bg-surface-container disabled:opacity-50" disabled>
              <span className="material-symbols-outlined text-[18px]">chevron_left</span>
            </button>
            <button className="w-8 h-8 rounded flex items-center justify-center bg-primary text-on-primary text-button">1</button>
            <button className="w-8 h-8 rounded flex items-center justify-center hover:bg-surface-container text-button text-on-surface">2</button>
            <button className="w-8 h-8 rounded flex items-center justify-center hover:bg-surface-container text-button text-on-surface">3</button>
            <span className="px-1 text-on-surface-variant">...</span>
            <button className="w-8 h-8 rounded flex items-center justify-center border border-outline-variant bg-surface-container-lowest text-on-surface-variant hover:bg-surface-container">
              <span className="material-symbols-outlined text-[18px]">chevron_right</span>
            </button>
          </div>
        </div>
      </div>

      {/* Add Modal */}
      {showAddModal && (
        <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/40 backdrop-blur-sm p-4">
          <div className="bg-surface-container-lowest rounded-xl shadow-[var(--shadow-float)] border border-border-muted p-xl w-full max-w-3xl">
            <h3 className="text-h3 text-on-surface mb-md">Mở Lớp học phần mới</h3>
            <form className="space-y-4" onSubmit={handleAdd}>
              <div className="grid grid-cols-2 gap-4">
                <div>
                  <label className="block text-label font-label text-on-surface-variant mb-1">Mã Lớp</label>
                  <input type="text" value={form.id} onChange={e => setForm({...form, id: e.target.value})} className="w-full bg-surface border border-outline-variant rounded-lg px-3 py-2 focus:border-primary outline-none" required placeholder="VD: CS101.A1" />
                </div>
                <div>
                  <label className="block text-label font-label text-on-surface-variant mb-1">Tên Môn học</label>
                  <input type="text" value={form.name} onChange={e => setForm({...form, name: e.target.value})} className="w-full bg-surface border border-outline-variant rounded-lg px-3 py-2 focus:border-primary outline-none" required placeholder="VD: Nhập môn Lập trình" />
                </div>
              </div>
              <div className="grid grid-cols-2 gap-4">
                <div>
                  <label className="block text-label font-label text-on-surface-variant mb-1">Giảng viên phụ trách</label>
                  <input type="text" value={form.teacher} onChange={e => setForm({...form, teacher: e.target.value})} className="w-full bg-surface border border-outline-variant rounded-lg px-3 py-2 focus:border-primary outline-none" required placeholder="VD: Nguyễn Văn A" />
                </div>
                <div>
                  <label className="block text-label font-label text-on-surface-variant mb-1">Sĩ số dự kiến</label>
                  <input type="text" value={form.size} onChange={e => setForm({...form, size: e.target.value})} className="w-full bg-surface border border-outline-variant rounded-lg px-3 py-2 focus:border-primary outline-none" required placeholder="VD: 0/50" />
                </div>
              </div>
              <div>
                <label className="block text-label font-label text-on-surface-variant mb-1">Phòng học (mặc định)</label>
                <input type="text" value={form.room} onChange={e => setForm({...form, room: e.target.value})} className="w-full bg-surface border border-outline-variant rounded-lg px-3 py-2 focus:border-primary outline-none" required placeholder="VD: Phòng 302, Tòa A" />
              </div>
              <div className="flex gap-3 justify-end pt-4 border-t border-outline-variant/30">
                <button type="button" onClick={() => setShowAddModal(false)} className="px-4 py-2 rounded-lg border border-outline-variant hover:bg-surface-container transition-colors text-button text-on-surface">Hủy</button>
                <button type="submit" className="px-4 py-2 rounded-lg bg-primary text-on-primary hover:bg-primary/90 transition-colors text-button">Mở Lớp</button>
              </div>
            </form>
          </div>
        </div>
      )}
    </main>
  );
}
