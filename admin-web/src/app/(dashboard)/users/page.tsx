"use client";

import React from "react";
import Link from "next/link";

export default function UsersPage() {
  const users = [
    { id: "SV2023001", name: "Nguyễn Văn A", initial: "N", email: "nva@universe.edu.vn", role: "Sinh viên", department: "Công nghệ Thông tin", status: "Đang học", statusType: "success" as const, initialColor: "bg-primary-container/20 text-primary" },
    { id: "GV1980045", name: "Trần Thị B", initial: "T", email: "ttb@universe.edu.vn", role: "Giảng viên", department: "Khoa học Cơ bản", status: "Đang giảng dạy", statusType: "success" as const, initialColor: "bg-tertiary-container/20 text-tertiary" },
    { id: "SV2023089", name: "Lê Minh C", initial: "L", email: "lmc@universe.edu.vn", role: "Sinh viên", department: "Kinh tế", status: "Đình chỉ", statusType: "error" as const, initialColor: "bg-primary-container/20 text-primary" },
  ];

  const getStatusStyle = (type: string) => {
    switch (type) {
      case "success": return "bg-secondary-container text-on-secondary-container";
      case "error": return "bg-error-container text-on-error-container";
      default: return "bg-surface-container-high text-on-surface-variant";
    }
  };

  return (
    <main className="flex-1 overflow-y-auto mt-16 p-xl bg-background">
      {/* Page Header */}
      <div className="flex justify-between items-end mb-lg">
        <div>
          <h2 className="text-h1 text-on-surface">Quản lý người dùng</h2>
          <p className="text-body-md text-on-surface-variant mt-1">
            Quản lý sinh viên, giảng viên và nhân viên hệ thống
          </p>
        </div>
        <Link
          href="/users/new"
          className="bg-primary hover:bg-surface-tint text-on-primary text-button px-4 py-2 rounded-lg flex items-center gap-2 shadow-sm transition-colors duration-200"
        >
          <span className="material-symbols-outlined text-[20px]">add</span>
          Thêm người dùng
        </Link>
      </div>

      {/* Data Card */}
      <div className="bg-surface-container-lowest rounded-xl shadow-[var(--shadow-card)] border border-border-muted overflow-hidden flex flex-col">
        {/* Toolbar */}
        <div className="p-md border-b border-border-muted flex justify-between items-center gap-4">
          <div className="relative w-96">
            <span className="material-symbols-outlined absolute left-3 top-1/2 -translate-y-1/2 text-on-surface-variant">search</span>
            <input
              className="w-full pl-10 pr-4 py-2 bg-surface rounded-lg border border-outline-variant focus:border-primary focus:ring-1 focus:ring-primary outline-none transition-all text-body-sm text-on-surface placeholder:text-on-surface-variant/70"
              placeholder="Tìm kiếm theo MSSV/MGV, Tên, Email..."
              type="text"
            />
          </div>
          <div className="flex items-center gap-2">
            <button className="px-3 py-2 bg-surface border border-outline-variant rounded-lg flex items-center gap-2 text-on-surface hover:bg-surface-container transition-colors text-body-sm">
              <span className="material-symbols-outlined text-[18px]">filter_list</span>
              Lọc theo vai trò
            </button>
            <button className="px-3 py-2 bg-surface border border-outline-variant rounded-lg flex items-center gap-2 text-on-surface hover:bg-surface-container transition-colors text-body-sm">
              <span className="material-symbols-outlined text-[18px]">sort</span>
              Sắp xếp
            </button>
          </div>
        </div>

        {/* Table */}
        <div className="flex-1 overflow-auto">
          <table className="w-full text-left border-collapse">
            <thead className="bg-surface sticky top-0 z-10 border-b border-border-muted">
              <tr>
                <th className="py-3 px-4 text-label text-on-surface-variant uppercase tracking-wider">MSSV/MGV</th>
                <th className="py-3 px-4 text-label text-on-surface-variant uppercase tracking-wider">HỌ TÊN</th>
                <th className="py-3 px-4 text-label text-on-surface-variant uppercase tracking-wider">EMAIL</th>
                <th className="py-3 px-4 text-label text-on-surface-variant uppercase tracking-wider">VAI TRÒ</th>
                <th className="py-3 px-4 text-label text-on-surface-variant uppercase tracking-wider">KHOA</th>
                <th className="py-3 px-4 text-label text-on-surface-variant uppercase tracking-wider">TRẠNG THÁI</th>
                <th className="py-3 px-4 text-label text-on-surface-variant uppercase tracking-wider text-right">THAO TÁC</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-outline-variant/20 text-body-sm text-on-surface">
              {users.map((user, idx) => (
                <tr key={idx} className={`hover:bg-surface-container-lowest/50 transition-colors ${idx % 2 === 1 ? 'bg-surface' : ''}`}>
                  <td className="py-3 px-4 font-medium">{user.id}</td>
                  <td className="py-3 px-4">
                    <div className="flex items-center gap-3">
                      <div className={`w-8 h-8 rounded-full ${user.initialColor} flex items-center justify-center font-bold text-sm`}>
                        {user.initial}
                      </div>
                      {user.name}
                    </div>
                  </td>
                  <td className="py-3 px-4 text-on-surface-variant">{user.email}</td>
                  <td className="py-3 px-4">{user.role}</td>
                  <td className="py-3 px-4">{user.department}</td>
                  <td className="py-3 px-4">
                    <span className={`inline-flex items-center px-2 py-1 rounded-full text-label text-[10px] ${getStatusStyle(user.statusType)}`}>
                      {user.status}
                    </span>
                  </td>
                  <td className="py-3 px-4 text-right">
                    <button className="text-on-surface-variant hover:text-primary p-1 transition-colors">
                      <span className="material-symbols-outlined text-[18px]">edit</span>
                    </button>
                    <button className="text-on-surface-variant hover:text-error p-1 ml-1 transition-colors">
                      <span className="material-symbols-outlined text-[18px]">delete</span>
                    </button>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>

        {/* Pagination */}
        <div className="p-md border-t border-border-muted bg-surface flex justify-between items-center">
          <p className="text-body-sm text-on-surface-variant">Hiển thị 1 - 3 trong số 120 người dùng</p>
          <div className="flex items-center gap-1">
            <button className="w-8 h-8 rounded flex items-center justify-center border border-outline-variant bg-surface-container-lowest text-on-surface-variant hover:bg-surface-container disabled:opacity-50" disabled>
              <span className="material-symbols-outlined text-[18px]">chevron_left</span>
            </button>
            <button className="w-8 h-8 rounded flex items-center justify-center bg-primary text-on-primary text-button">1</button>
            <button className="w-8 h-8 rounded flex items-center justify-center hover:bg-surface-container text-button text-on-surface">2</button>
            <button className="w-8 h-8 rounded flex items-center justify-center hover:bg-surface-container text-button text-on-surface">3</button>
            <span className="px-1 text-on-surface-variant">...</span>
            <button className="w-8 h-8 rounded flex items-center justify-center hover:bg-surface-container text-button text-on-surface">12</button>
            <button className="w-8 h-8 rounded flex items-center justify-center border border-outline-variant bg-surface-container-lowest text-on-surface-variant hover:bg-surface-container">
              <span className="material-symbols-outlined text-[18px]">chevron_right</span>
            </button>
          </div>
        </div>
      </div>
    </main>
  );
}
