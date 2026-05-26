"use client";

import React, { useState } from "react";
import Link from "next/link";

interface Department {
  id: string;
  name: string;
  code: string;
  headOfDepartment: string;
  coursesCount: number;
}

const mockDepartments: Department[] = [
  { id: "1", name: "Khoa Công nghệ thông tin", code: "IT", headOfDepartment: "PGS.TS. Trần Văn A", coursesCount: 24 },
  { id: "2", name: "Khoa Quản trị kinh doanh", code: "BA", headOfDepartment: "TS. Nguyễn Thị B", coursesCount: 18 },
  { id: "3", name: "Khoa Thiết kế đồ họa", code: "GD", headOfDepartment: "ThS. Lê Văn C", coursesCount: 15 },
  { id: "4", name: "Khoa Ngoại ngữ", code: "FL", headOfDepartment: "TS. Phạm Thị D", coursesCount: 20 },
];

export default function DepartmentsPage() {
  const [departments, setDepartments] = useState<Department[]>(mockDepartments);
  const [search, setSearch] = useState("");
  const [showAddModal, setShowAddModal] = useState(false);
  const [form, setForm] = useState({ code: "", name: "", headOfDepartment: "" });
  
  const [showEditModal, setShowEditModal] = useState(false);
  const [editTarget, setEditTarget] = useState<Department | null>(null);

  const filteredDepts = departments.filter(d => 
    d.name.toLowerCase().includes(search.toLowerCase()) || 
    d.code.toLowerCase().includes(search.toLowerCase())
  );

  const handleAdd = (e: React.FormEvent) => {
    e.preventDefault();
    setDepartments([...departments, {
      id: Math.random().toString(),
      ...form,
      coursesCount: 0
    }]);
    setShowAddModal(false);
    setForm({ code: "", name: "", headOfDepartment: "" });
  };

  const openEditModal = (dept: Department) => {
    setEditTarget(dept);
    setForm({ code: dept.code, name: dept.name, headOfDepartment: dept.headOfDepartment });
    setShowEditModal(true);
  };

  const handleEdit = (e: React.FormEvent) => {
    e.preventDefault();
    if (!editTarget) return;
    setDepartments(departments.map(d => d.id === editTarget.id ? { ...d, ...form } : d));
    setShowEditModal(false);
    setEditTarget(null);
    setForm({ code: "", name: "", headOfDepartment: "" });
  };

  return (
    <main className="flex-1 overflow-y-auto mt-16 p-xl bg-background">
      {/* Header */}
      <div className="flex justify-between items-end mb-lg">
        <div>
          <h2 className="text-h1 text-on-surface">Khoa & Ngành</h2>
          <p className="text-body-md text-on-surface-variant mt-1">
            Quản lý cơ cấu tổ chức các khoa trong trường
          </p>
        </div>
        <div className="flex gap-2">
          <button onClick={() => setShowAddModal(true)} className="bg-primary hover:bg-surface-tint text-on-primary text-button px-4 py-2 rounded-lg flex items-center gap-2 shadow-sm transition-colors duration-200">
            <span className="material-symbols-outlined text-[20px]">add</span>
            Thêm Khoa mới
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
              placeholder="Tìm kiếm khoa..."
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
                <th className="py-3 px-4 text-label text-on-surface-variant uppercase tracking-wider">Mã Khoa</th>
                <th className="py-3 px-4 text-label text-on-surface-variant uppercase tracking-wider">Tên Khoa</th>
                <th className="py-3 px-4 text-label text-on-surface-variant uppercase tracking-wider">Trưởng khoa</th>
                <th className="py-3 px-4 text-label text-on-surface-variant uppercase tracking-wider">Số môn học</th>
                <th className="py-3 px-4 text-label text-on-surface-variant uppercase tracking-wider text-right">Thao tác</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-outline-variant/20 text-body-sm text-on-surface">
              {filteredDepts.map(dept => (
                <tr key={dept.id} className="hover:bg-surface-container-lowest transition-colors">
                  <td className="py-3 px-4 font-medium">{dept.code}</td>
                  <td className="py-3 px-4">{dept.name}</td>
                  <td className="py-3 px-4 text-on-surface-variant">{dept.headOfDepartment}</td>
                  <td className="py-3 px-4 text-on-surface-variant">{dept.coursesCount}</td>
                  <td className="py-3 px-4 text-right">
                    <div className="flex justify-end gap-2">
                      <button onClick={() => openEditModal(dept)} className="p-1.5 text-on-surface-variant hover:text-primary hover:bg-primary-container/20 rounded-md transition-colors" title="Chỉnh sửa">
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
            <h3 className="text-h3 text-on-surface mb-md">Thêm Khoa mới</h3>
            <form className="space-y-4" onSubmit={handleAdd}>
              <div className="grid grid-cols-2 gap-4">
                <div>
                  <label className="block text-label font-label text-on-surface-variant mb-1">Mã Khoa</label>
                  <input type="text" value={form.code} onChange={e => setForm({...form, code: e.target.value})} className="w-full bg-surface border border-outline-variant rounded-lg px-3 py-2 focus:border-primary outline-none" required placeholder="VD: IT" />
                </div>
                <div>
                  <label className="block text-label font-label text-on-surface-variant mb-1">Tên Khoa</label>
                  <input type="text" value={form.name} onChange={e => setForm({...form, name: e.target.value})} className="w-full bg-surface border border-outline-variant rounded-lg px-3 py-2 focus:border-primary outline-none" required placeholder="VD: Khoa Công nghệ thông tin" />
                </div>
              </div>
              <div>
                <label className="block text-label font-label text-on-surface-variant mb-1">Trưởng khoa</label>
                <input type="text" value={form.headOfDepartment} onChange={e => setForm({...form, headOfDepartment: e.target.value})} className="w-full bg-surface border border-outline-variant rounded-lg px-3 py-2 focus:border-primary outline-none" required placeholder="VD: PGS.TS Trần Văn A" />
              </div>
              <div className="flex gap-3 justify-end pt-4 border-t border-outline-variant/30">
                <button type="button" onClick={() => setShowAddModal(false)} className="px-4 py-2 rounded-lg border border-outline-variant hover:bg-surface-container transition-colors text-button text-on-surface">Hủy</button>
                <button type="submit" className="px-4 py-2 rounded-lg bg-primary text-on-primary hover:bg-primary/90 transition-colors text-button">Thêm Khoa</button>
              </div>
            </form>
          </div>
        </div>
      )}

      {/* Edit Modal */}
      {showEditModal && (
        <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/40 backdrop-blur-sm p-4">
          <div className="bg-surface-container-lowest rounded-xl shadow-[var(--shadow-float)] border border-border-muted p-xl w-full max-w-3xl">
            <h3 className="text-h3 text-on-surface mb-md">Cập nhật Khoa & Ngành</h3>
            <form className="space-y-4" onSubmit={handleEdit}>
              <div className="grid grid-cols-2 gap-4">
                <div>
                  <label className="block text-label font-label text-on-surface-variant mb-1">Mã Khoa</label>
                  <input type="text" value={form.code} onChange={e => setForm({...form, code: e.target.value})} className="w-full bg-surface border border-outline-variant rounded-lg px-3 py-2 focus:border-primary outline-none" required />
                </div>
                <div>
                  <label className="block text-label font-label text-on-surface-variant mb-1">Tên Khoa</label>
                  <input type="text" value={form.name} onChange={e => setForm({...form, name: e.target.value})} className="w-full bg-surface border border-outline-variant rounded-lg px-3 py-2 focus:border-primary outline-none" required />
                </div>
              </div>
              <div>
                <label className="block text-label font-label text-on-surface-variant mb-1">Trưởng khoa</label>
                <input type="text" value={form.headOfDepartment} onChange={e => setForm({...form, headOfDepartment: e.target.value})} className="w-full bg-surface border border-outline-variant rounded-lg px-3 py-2 focus:border-primary outline-none" required />
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
