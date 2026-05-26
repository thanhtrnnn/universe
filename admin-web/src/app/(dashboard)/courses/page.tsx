"use client";

import React, { useState } from "react";

interface Course {
  id: string;
  code: string;
  name: string;
  credits: number;
  department: string;
}

const mockCourses: Course[] = [
  { id: "1", code: "IT101", name: "Nhập môn Lập trình", credits: 3, department: "Khoa Công nghệ thông tin" },
  { id: "2", code: "IT202", name: "Cấu trúc dữ liệu và giải thuật", credits: 4, department: "Khoa Công nghệ thông tin" },
  { id: "3", code: "BA101", name: "Kinh tế vi mô", credits: 3, department: "Khoa Quản trị kinh doanh" },
  { id: "4", code: "GD101", name: "Nguyên lý thiết kế", credits: 2, department: "Khoa Thiết kế đồ họa" },
  { id: "5", code: "FL101", name: "Tiếng Anh giao tiếp", credits: 3, department: "Khoa Ngoại ngữ" },
];

export default function CoursesPage() {
  const [courses, setCourses] = useState<Course[]>(mockCourses);
  const [search, setSearch] = useState("");
  const [showAddModal, setShowAddModal] = useState(false);
  const [form, setForm] = useState({ code: "", name: "", credits: "3", department: "Khoa Công nghệ thông tin" });
  
  const [showEditModal, setShowEditModal] = useState(false);
  const [editTarget, setEditTarget] = useState<Course | null>(null);

  const filteredCourses = courses.filter(c => 
    c.name.toLowerCase().includes(search.toLowerCase()) || 
    c.code.toLowerCase().includes(search.toLowerCase()) ||
    c.department.toLowerCase().includes(search.toLowerCase())
  );

  const handleAdd = (e: React.FormEvent) => {
    e.preventDefault();
    setCourses([...courses, {
      id: Math.random().toString(),
      code: form.code,
      name: form.name,
      credits: parseInt(form.credits),
      department: form.department
    }]);
    setShowAddModal(false);
    setForm({ code: "", name: "", credits: "3", department: "Khoa Công nghệ thông tin" });
  };

  const openEditModal = (course: Course) => {
    setEditTarget(course);
    setForm({ code: course.code, name: course.name, credits: course.credits.toString(), department: course.department });
    setShowEditModal(true);
  };

  const handleEdit = (e: React.FormEvent) => {
    e.preventDefault();
    if (!editTarget) return;
    setCourses(courses.map(c => c.id === editTarget.id ? {
      ...c,
      code: form.code,
      name: form.name,
      credits: parseInt(form.credits),
      department: form.department
    } : c));
    setShowEditModal(false);
    setEditTarget(null);
    setForm({ code: "", name: "", credits: "3", department: "Khoa Công nghệ thông tin" });
  };

  return (
    <main className="flex-1 overflow-y-auto mt-16 p-xl bg-background">
      {/* Header */}
      <div className="flex justify-between items-end mb-lg">
        <div>
          <h2 className="text-h1 text-on-surface">Môn học</h2>
          <p className="text-body-md text-on-surface-variant mt-1">
            Quản lý danh mục các môn học trong trường
          </p>
        </div>
        <div className="flex gap-2">
          <button onClick={() => setShowAddModal(true)} className="bg-primary hover:bg-surface-tint text-on-primary text-button px-4 py-2 rounded-lg flex items-center gap-2 shadow-sm transition-colors duration-200">
            <span className="material-symbols-outlined text-[20px]">add</span>
            Thêm Môn học
          </button>
        </div>
      </div>

      {/* Data Card */}
      <div className="bg-surface-container-lowest rounded-xl shadow-[var(--shadow-card)] border border-border-muted overflow-hidden flex flex-col">
        {/* Toolbar */}
        <div className="p-md border-b border-border-muted flex gap-3 items-center">
          <div className="relative w-full sm:w-96">
            <span className="material-symbols-outlined absolute left-3 top-1/2 -translate-y-1/2 text-on-surface-variant text-[20px]">search</span>
            <input
              className="w-full pl-10 pr-4 py-2 bg-surface rounded-lg border border-outline-variant focus:border-primary focus:ring-1 focus:ring-primary outline-none transition-all text-body-sm text-on-surface placeholder:text-on-surface-variant/70"
              placeholder="Tìm kiếm môn học hoặc khoa..."
              type="text"
              value={search}
              onChange={(e) => setSearch(e.target.value)}
            />
          </div>
        </div>

        {/* Table */}
        <div className="flex-1 overflow-auto">
          <table className="w-full text-left border-collapse">
            <thead className="bg-surface sticky top-0 z-10 border-b border-border-muted">
              <tr>
                <th className="py-3 px-4 text-label text-on-surface-variant uppercase tracking-wider">Mã Môn</th>
                <th className="py-3 px-4 text-label text-on-surface-variant uppercase tracking-wider">Tên Môn học</th>
                <th className="py-3 px-4 text-label text-on-surface-variant uppercase tracking-wider">Số tín chỉ</th>
                <th className="py-3 px-4 text-label text-on-surface-variant uppercase tracking-wider">Thuộc Khoa</th>
                <th className="py-3 px-4 text-label text-on-surface-variant uppercase tracking-wider text-right">Thao tác</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-outline-variant/20 text-body-sm text-on-surface">
              {filteredCourses.map(course => (
                <tr key={course.id} className="hover:bg-surface-container-lowest transition-colors">
                  <td className="py-3 px-4 font-medium">{course.code}</td>
                  <td className="py-3 px-4">{course.name}</td>
                  <td className="py-3 px-4 text-on-surface-variant">{course.credits}</td>
                  <td className="py-3 px-4">
                    <span className="inline-flex items-center px-2 py-1 rounded-md bg-surface-container-high text-on-surface-variant text-body-sm">
                      {course.department}
                    </span>
                  </td>
                  <td className="py-3 px-4 text-right">
                    <div className="flex justify-end gap-2">
                      <button onClick={() => openEditModal(course)} className="p-1.5 text-on-surface-variant hover:text-primary hover:bg-primary-container/20 rounded-md transition-colors" title="Chỉnh sửa">
                        <span className="material-symbols-outlined text-[18px]">edit</span>
                      </button>
                      <button className="p-1.5 text-on-surface-variant hover:text-error hover:bg-error-container/20 rounded-md transition-colors" title="Xóa">
                        <span className="material-symbols-outlined text-[18px]">delete</span>
                      </button>
                    </div>
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
            <h3 className="text-h3 text-on-surface mb-md">Thêm Môn học mới</h3>
            <form className="space-y-4" onSubmit={handleAdd}>
              <div className="grid grid-cols-2 gap-4">
                <div>
                  <label className="block text-label font-label text-on-surface-variant mb-1">Mã Môn</label>
                  <input type="text" value={form.code} onChange={e => setForm({...form, code: e.target.value})} className="w-full bg-surface border border-outline-variant rounded-lg px-3 py-2 focus:border-primary outline-none" required placeholder="VD: IT102" />
                </div>
                <div>
                  <label className="block text-label font-label text-on-surface-variant mb-1">Tên Môn học</label>
                  <input type="text" value={form.name} onChange={e => setForm({...form, name: e.target.value})} className="w-full bg-surface border border-outline-variant rounded-lg px-3 py-2 focus:border-primary outline-none" required placeholder="VD: Nhập môn Lập trình" />
                </div>
              </div>
              <div className="grid grid-cols-2 gap-4">
                <div>
                  <label className="block text-label font-label text-on-surface-variant mb-1">Số tín chỉ</label>
                  <input type="number" min="1" max="10" value={form.credits} onChange={e => setForm({...form, credits: e.target.value})} className="w-full bg-surface border border-outline-variant rounded-lg px-3 py-2 focus:border-primary outline-none" required />
                </div>
                <div>
                  <label className="block text-label font-label text-on-surface-variant mb-1">Thuộc Khoa</label>
                  <input type="text" value={form.department} onChange={e => setForm({...form, department: e.target.value})} className="w-full bg-surface border border-outline-variant rounded-lg px-3 py-2 focus:border-primary outline-none" required placeholder="VD: Khoa Công nghệ thông tin" />
                </div>
              </div>
              <div className="flex gap-3 justify-end pt-4 border-t border-outline-variant/30">
                <button type="button" onClick={() => setShowAddModal(false)} className="px-4 py-2 rounded-lg border border-outline-variant hover:bg-surface-container transition-colors text-button text-on-surface">Hủy</button>
                <button type="submit" className="px-4 py-2 rounded-lg bg-primary text-on-primary hover:bg-primary/90 transition-colors text-button">Thêm Môn học</button>
              </div>
            </form>
          </div>
        </div>
      )}

      {/* Edit Modal */}
      {showEditModal && (
        <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/40 backdrop-blur-sm p-4">
          <div className="bg-surface-container-lowest rounded-xl shadow-[var(--shadow-float)] border border-border-muted p-xl w-full max-w-3xl">
            <h3 className="text-h3 text-on-surface mb-md">Cập nhật Môn học</h3>
            <form className="space-y-4" onSubmit={handleEdit}>
              <div className="grid grid-cols-2 gap-4">
                <div>
                  <label className="block text-label font-label text-on-surface-variant mb-1">Mã Môn</label>
                  <input type="text" value={form.code} onChange={e => setForm({...form, code: e.target.value})} className="w-full bg-surface border border-outline-variant rounded-lg px-3 py-2 focus:border-primary outline-none" required />
                </div>
                <div>
                  <label className="block text-label font-label text-on-surface-variant mb-1">Tên Môn học</label>
                  <input type="text" value={form.name} onChange={e => setForm({...form, name: e.target.value})} className="w-full bg-surface border border-outline-variant rounded-lg px-3 py-2 focus:border-primary outline-none" required />
                </div>
              </div>
              <div className="grid grid-cols-2 gap-4">
                <div>
                  <label className="block text-label font-label text-on-surface-variant mb-1">Số tín chỉ</label>
                  <input type="number" min="1" max="10" value={form.credits} onChange={e => setForm({...form, credits: e.target.value})} className="w-full bg-surface border border-outline-variant rounded-lg px-3 py-2 focus:border-primary outline-none" required />
                </div>
                <div>
                  <label className="block text-label font-label text-on-surface-variant mb-1">Thuộc Khoa</label>
                  <input type="text" value={form.department} onChange={e => setForm({...form, department: e.target.value})} className="w-full bg-surface border border-outline-variant rounded-lg px-3 py-2 focus:border-primary outline-none" required />
                </div>
              </div>
              <div className="flex gap-3 justify-end pt-4 border-t border-outline-variant/30">
                <button type="button" onClick={() => setShowEditModal(false)} className="px-4 py-2 rounded-lg border border-outline-variant hover:bg-surface-container transition-colors text-button text-on-surface">Hủy</button>
                <button type="submit" className="px-4 py-2 rounded-lg bg-primary text-on-primary hover:bg-primary/90 transition-colors text-button">Lưu thay đổi</button>
              </div>
            </form>
          </div>
        </div>
      )}
    </main>
  );
}
