"use client";
import React, { useState } from "react";
import Link from "next/link";

export default function LecturerClassesPage() {
  const [showAddModal, setShowAddModal] = useState(false);
  const [form, setForm] = useState({ code: "", name: "", credits: "3 Tín chỉ", schedule: "", room: "" });
  const [classes, setClasses] = useState([
    { code: "CS101.O11", name: "Nhập môn Lập trình", credits: "3 Tín chỉ", students: "48/50", schedule: "Thứ 3, Ca 1 (07:30 - 09:30)", room: "A1-204", status: "Đang học", statusType: "success" },
    { code: "MA102.M21", name: "Toán Cao cấp A2", credits: "4 Tín chỉ", students: "60/60", schedule: "Thứ 5, Ca 3 (13:00 - 15:30)", room: "C2-301", status: "Đang học", statusType: "success" },
    { code: "SE304.O12", name: "Phân tích thiết kế hệ thống", credits: "3 Tín chỉ", students: "35/40", schedule: "Thứ 6, Ca 2 (09:30 - 11:30)", room: "B3-105", status: "Kết thúc", statusType: "neutral" },
    { code: "DB205.M11", name: "Hệ quản trị Cơ sở dữ liệu", credits: "3 Tín chỉ", students: "52/60", schedule: "Thứ 4, Ca 1 (07:30 - 09:30)", room: "A1-302", status: "Đang học", statusType: "success" },
  ]);

  const handleAdd = (e: React.FormEvent) => {
    e.preventDefault();
    setClasses([...classes, {
      ...form,
      students: "0/50",
      status: "Đang học",
      statusType: "success"
    }]);
    setShowAddModal(false);
    setForm({ code: "", name: "", credits: "3 Tín chỉ", schedule: "", room: "" });
  };

  return (
    <main className="flex-1 overflow-y-auto mt-16 p-xl bg-background">
      {/* Page Header */}
      <div className="mb-lg flex flex-col md:flex-row md:items-center justify-between gap-4">
        <div>
          <h2 className="font-h1 text-h1 text-on-surface mb-xs">Danh sách lớp phụ trách</h2>
          <p className="font-body-md text-body-md text-on-surface-variant">Quản lý và theo dõi thông tin các lớp học đang được phân công giảng dạy.</p>
        </div>
        <div className="flex items-center gap-md">
          <button className="h-10 px-4 flex items-center gap-2 bg-surface-container-lowest border border-outline-variant text-on-surface rounded-lg font-button text-button hover:bg-surface-container-low transition-colors">
            <span className="material-symbols-outlined text-[20px]">download</span>
            Xuất báo cáo
          </button>
          <button 
            onClick={() => setShowAddModal(true)}
            className="h-10 px-4 flex items-center gap-2 bg-primary text-on-primary rounded-lg font-button text-button shadow-sm hover:bg-primary-container hover:text-on-primary-container transition-colors"
          >
            <span className="material-symbols-outlined text-[20px]">add</span>
            Tạo lớp phụ đạo
          </button>
        </div>
      </div>

      {/* Filters Section */}
      <div className="bg-surface-container-lowest border border-surface-container-highest rounded-xl p-md mb-lg shadow-[0px_4px_20px_rgba(26,26,26,0.05)] flex flex-wrap items-center gap-md">
        <div className="flex-1 min-w-[200px]">
          <label className="block font-label text-label text-on-surface-variant mb-xs uppercase">Học kỳ</label>
          <div className="relative">
            <select className="w-full appearance-none bg-surface border border-outline-variant rounded-lg pl-4 pr-10 py-2.5 font-body-md text-body-md text-on-surface focus:outline-none focus:border-primary focus:ring-1 focus:ring-primary cursor-pointer transition-colors">
              <option>Học kỳ 1, 2024-2025</option>
              <option>Học kỳ 2, 2023-2024</option>
              <option>Học kỳ 1, 2023-2024</option>
            </select>
            <span className="material-symbols-outlined absolute right-3 top-1/2 -translate-y-1/2 text-outline-variant pointer-events-none">expand_more</span>
          </div>
        </div>
        <div className="flex-1 min-w-[200px]">
          <label className="block font-label text-label text-on-surface-variant mb-xs uppercase">Trạng thái lớp</label>
          <div className="relative">
            <select defaultValue="Đang học" className="w-full appearance-none bg-surface border border-outline-variant rounded-lg pl-4 pr-10 py-2.5 font-body-md text-body-md text-on-surface focus:outline-none focus:border-primary focus:ring-1 focus:ring-primary cursor-pointer transition-colors">
              <option value="Tất cả trạng thái">Tất cả trạng thái</option>
              <option value="Đang học">Đang học</option>
              <option value="Kết thúc">Kết thúc</option>
            </select>
            <span className="material-symbols-outlined absolute right-3 top-1/2 -translate-y-1/2 text-outline-variant pointer-events-none">expand_more</span>
          </div>
        </div>
        <div className="flex-[2] min-w-[300px]">
          <label className="block font-label text-label text-on-surface-variant mb-xs uppercase">Tìm kiếm theo mã/tên</label>
          <div className="relative group">
            <span className="material-symbols-outlined absolute left-3 top-1/2 -translate-y-1/2 text-outline-variant group-focus-within:text-primary transition-colors">search</span>
            <input className="w-full pl-10 pr-4 py-2.5 bg-surface border border-outline-variant rounded-lg font-body-md text-body-md text-on-surface focus:outline-none focus:border-primary focus:ring-1 focus:ring-primary transition-all" placeholder="Nhập mã lớp hoặc tên môn học..." type="text" />
          </div>
        </div>
      </div>

      {/* Data Table */}
      <div className="bg-surface-container-lowest rounded-xl shadow-[0px_4px_20px_rgba(26,26,26,0.05)] border border-[#EEEDFE] overflow-hidden">
        <div className="overflow-x-auto">
          <table className="w-full text-left border-collapse">
            <thead className="bg-surface-container-low border-b border-surface-container-highest">
              <tr>
                <th className="py-4 px-6 font-label text-label text-on-surface-variant uppercase whitespace-nowrap">Mã lớp</th>
                <th className="py-4 px-6 font-label text-label text-on-surface-variant uppercase whitespace-nowrap">Tên môn học</th>
                <th className="py-4 px-6 font-label text-label text-on-surface-variant uppercase whitespace-nowrap">Sĩ số</th>
                <th className="py-4 px-6 font-label text-label text-on-surface-variant uppercase whitespace-nowrap">Lịch học</th>
                <th className="py-4 px-6 font-label text-label text-on-surface-variant uppercase whitespace-nowrap">Phòng</th>
                <th className="py-4 px-6 font-label text-label text-on-surface-variant uppercase whitespace-nowrap">Trạng thái</th>
                <th className="py-4 px-6 font-label text-label text-on-surface-variant uppercase whitespace-nowrap text-right">Thao tác</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-surface-container-highest">
              {classes.map((cls, idx) => (
                <tr key={idx} className="hover:bg-[#F8F7FF] transition-colors group">
                  <td className="py-4 px-6">
                    <div className="font-body-md font-semibold text-primary">{cls.code}</div>
                  </td>
                  <td className="py-4 px-6">
                    <div className="font-body-md text-on-surface font-medium">{cls.name}</div>
                    <div className="font-body-sm text-on-surface-variant mt-0.5">{cls.credits}</div>
                  </td>
                  <td className="py-4 px-6">
                    <div className="flex items-center gap-2">
                      <span className="material-symbols-outlined text-[18px] text-outline">group</span>
                      <span className="font-body-md text-on-surface">{cls.students}</span>
                    </div>
                  </td>
                  <td className="py-4 px-6 text-body-md text-on-surface">{cls.schedule}</td>
                  <td className="py-4 px-6">
                    <div className="inline-flex items-center gap-1.5 px-2.5 py-1 rounded bg-surface-container text-on-surface font-body-sm">
                      <span className="material-symbols-outlined text-[16px]">location_on</span>
                      {cls.room}
                    </div>
                  </td>
                  <td className="py-4 px-6">
                    <span className={`inline-flex items-center px-2.5 py-1 rounded-full text-xs font-semibold ${
                      cls.statusType === 'success' ? 'bg-secondary-container text-on-secondary-container' : 'bg-surface-container-highest text-on-surface-variant'
                    }`}>
                      {cls.status}
                    </span>
                  </td>
                  <td className="py-4 px-6 text-right">
                    <Link href="/attendance" className="inline-flex items-center justify-center h-8 px-3 rounded-md bg-transparent border border-outline-variant text-on-surface hover:bg-primary hover:border-primary hover:text-on-primary font-button text-button transition-colors">
                      Chi tiết
                    </Link>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      </div>

      {/* Add Modal */}
      {showAddModal && (
        <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/40 backdrop-blur-sm p-4">
          <div className="bg-surface-container-lowest rounded-xl shadow-[var(--shadow-float)] border border-border-muted p-xl w-full max-w-3xl">
            <h3 className="text-h3 text-on-surface mb-md">Tạo lớp phụ đạo</h3>
            <form className="space-y-4" onSubmit={handleAdd}>
              <div className="grid grid-cols-2 gap-4">
                <div>
                  <label className="block text-label font-label text-on-surface-variant mb-1">Mã Lớp phụ đạo</label>
                  <input type="text" value={form.code} onChange={e => setForm({...form, code: e.target.value})} className="w-full bg-surface border border-outline-variant rounded-lg px-3 py-2 focus:border-primary outline-none" required placeholder="VD: CS101.PD1" />
                </div>
                <div>
                  <label className="block text-label font-label text-on-surface-variant mb-1">Tên Môn học</label>
                  <input type="text" value={form.name} onChange={e => setForm({...form, name: e.target.value})} className="w-full bg-surface border border-outline-variant rounded-lg px-3 py-2 focus:border-primary outline-none" required placeholder="VD: Phụ đạo Nhập môn Lập trình" />
                </div>
              </div>
              <div className="grid grid-cols-2 gap-4">
                <div>
                  <label className="block text-label font-label text-on-surface-variant mb-1">Lịch học</label>
                  <input type="text" value={form.schedule} onChange={e => setForm({...form, schedule: e.target.value})} className="w-full bg-surface border border-outline-variant rounded-lg px-3 py-2 focus:border-primary outline-none" required placeholder="VD: Thứ 7, Ca 1" />
                </div>
                <div>
                  <label className="block text-label font-label text-on-surface-variant mb-1">Phòng học</label>
                  <input type="text" value={form.room} onChange={e => setForm({...form, room: e.target.value})} className="w-full bg-surface border border-outline-variant rounded-lg px-3 py-2 focus:border-primary outline-none" required placeholder="VD: A1-102" />
                </div>
              </div>
              <div className="flex gap-3 justify-end pt-4 border-t border-outline-variant/30">
                <button type="button" onClick={() => setShowAddModal(false)} className="px-4 py-2 rounded-lg border border-outline-variant hover:bg-surface-container transition-colors text-button text-on-surface">Hủy</button>
                <button type="submit" className="px-4 py-2 rounded-lg bg-primary text-on-primary hover:bg-primary/90 transition-colors text-button">Tạo lớp</button>
              </div>
            </form>
          </div>
        </div>
      )}
    </main>
  );
}

