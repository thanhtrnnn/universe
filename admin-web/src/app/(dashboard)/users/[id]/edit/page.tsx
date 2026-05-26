"use client";

import React, { useState } from "react";
import Link from "next/link";
import { useRouter } from "next/navigation";

export default function EditUserPage({ params }: { params: { id: string } }) {
  const router = useRouter();
  
  // Mock data for the user being edited
  const [user, setUser] = useState({
    code: "SV001",
    name: "Nguyễn Văn An",
    email: "an.nv@universe.edu.vn",
    role: "student",
    status: "active",
    department: "Công nghệ thông tin",
    phone: "0901234567",
    address: "123 Đường Xuân Thủy, Cầu Giấy, Hà Nội"
  });

  const [isSaving, setIsSaving] = useState(false);

  const handleSave = (e: React.FormEvent) => {
    e.preventDefault();
    setIsSaving(true);
    // Simulate API call
    setTimeout(() => {
      setIsSaving(false);
      alert("Đã cập nhật thông tin người dùng thành công!");
      router.push("/users");
    }, 800);
  };

  return (
    <main className="flex-1 pt-20 px-xl pb-xl bg-background overflow-y-auto min-h-screen">
      {/* Back Navigation */}
      <Link 
        href="/users" 
        className="inline-flex items-center gap-sm text-on-surface-variant hover:text-primary transition-colors duration-200 mb-lg font-button text-button"
      >
        <span className="material-symbols-outlined text-[20px]">arrow_back</span>
        Quay lại danh sách người dùng
      </Link>

      <div className="flex justify-between items-end mb-xl">
        <div>
          <h2 className="text-h1 text-on-surface">Chỉnh sửa Người dùng</h2>
          <p className="text-body-md text-on-surface-variant mt-1">Cập nhật thông tin chi tiết và phân quyền tài khoản</p>
        </div>
      </div>

      <form onSubmit={handleSave} className="grid grid-cols-1 lg:grid-cols-3 gap-container_gutter">
        {/* Left Column: Personal Info */}
        <div className="lg:col-span-2 space-y-container_gutter">
          <div className="bg-surface-container-lowest rounded-xl shadow-[var(--shadow-float)] border border-border-muted p-xl">
            <h3 className="text-h3 text-on-surface mb-lg flex items-center gap-2 border-b border-border-muted pb-4">
              <span className="material-symbols-outlined text-primary">person</span>
              Thông tin cá nhân
            </h3>
            <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
              <div>
                <label className="block text-label font-label text-on-surface-variant mb-1">Họ và tên <span className="text-error">*</span></label>
                <input type="text" value={user.name} onChange={e => setUser({...user, name: e.target.value})} className="w-full bg-surface border border-outline-variant rounded-lg px-4 py-2 text-body-md focus:border-primary outline-none transition-colors" required />
              </div>
              <div>
                <label className="block text-label font-label text-on-surface-variant mb-1">Mã định danh (MSSV/MGV) <span className="text-error">*</span></label>
                <input type="text" value={user.code} onChange={e => setUser({...user, code: e.target.value})} className="w-full bg-surface border border-outline-variant rounded-lg px-4 py-2 text-body-md focus:border-primary outline-none transition-colors" required />
              </div>
              <div>
                <label className="block text-label font-label text-on-surface-variant mb-1">Email <span className="text-error">*</span></label>
                <input type="email" value={user.email} onChange={e => setUser({...user, email: e.target.value})} className="w-full bg-surface border border-outline-variant rounded-lg px-4 py-2 text-body-md focus:border-primary outline-none transition-colors" required />
              </div>
              <div>
                <label className="block text-label font-label text-on-surface-variant mb-1">Số điện thoại</label>
                <input type="tel" value={user.phone} onChange={e => setUser({...user, phone: e.target.value})} className="w-full bg-surface border border-outline-variant rounded-lg px-4 py-2 text-body-md focus:border-primary outline-none transition-colors" />
              </div>
              <div className="md:col-span-2">
                <label className="block text-label font-label text-on-surface-variant mb-1">Địa chỉ</label>
                <input type="text" value={user.address} onChange={e => setUser({...user, address: e.target.value})} className="w-full bg-surface border border-outline-variant rounded-lg px-4 py-2 text-body-md focus:border-primary outline-none transition-colors" />
              </div>
            </div>
          </div>
        </div>

        {/* Right Column: Account Settings & Roles */}
        <div className="space-y-container_gutter">
          <div className="bg-surface-container-lowest rounded-xl shadow-[var(--shadow-float)] border border-border-muted p-xl">
            <h3 className="text-h3 text-on-surface mb-lg flex items-center gap-2 border-b border-border-muted pb-4">
              <span className="material-symbols-outlined text-secondary">admin_panel_settings</span>
              Cài đặt tài khoản
            </h3>
            
            <div className="space-y-6">
              <div>
                <label className="block text-label font-label text-on-surface-variant mb-1">Vai trò</label>
                <div className="relative">
                  <select value={user.role} onChange={e => setUser({...user, role: e.target.value})} className="w-full appearance-none bg-surface border border-outline-variant rounded-lg px-4 py-2 text-body-md focus:border-primary outline-none transition-colors cursor-pointer">
                    <option value="student">Sinh viên</option>
                    <option value="lecturer">Giảng viên</option>
                    <option value="admin">Quản trị viên</option>
                  </select>
                  <span className="material-symbols-outlined absolute right-3 top-1/2 -translate-y-1/2 text-outline-variant pointer-events-none">expand_more</span>
                </div>
              </div>

              {user.role !== "admin" && (
                <div>
                  <label className="block text-label font-label text-on-surface-variant mb-1">Khoa / Viện</label>
                  <div className="relative">
                    <select value={user.department} onChange={e => setUser({...user, department: e.target.value})} className="w-full appearance-none bg-surface border border-outline-variant rounded-lg px-4 py-2 text-body-md focus:border-primary outline-none transition-colors cursor-pointer">
                      <option value="Công nghệ thông tin">Công nghệ thông tin</option>
                      <option value="Kinh tế">Kinh tế</option>
                      <option value="Ngoại ngữ">Ngoại ngữ</option>
                      <option value="Cơ khí">Cơ khí</option>
                    </select>
                    <span className="material-symbols-outlined absolute right-3 top-1/2 -translate-y-1/2 text-outline-variant pointer-events-none">expand_more</span>
                  </div>
                </div>
              )}

              <div>
                <label className="block text-label font-label text-on-surface-variant mb-1">Trạng thái hoạt động</label>
                <div className="relative">
                  <select value={user.status} onChange={e => setUser({...user, status: e.target.value})} className={`w-full appearance-none bg-surface border border-outline-variant rounded-lg px-4 py-2 text-body-md focus:border-primary outline-none transition-colors cursor-pointer font-medium ${user.status === 'active' ? 'text-secondary' : user.status === 'suspended' ? 'text-error' : 'text-on-surface-variant'}`}>
                    <option value="active">Đang hoạt động</option>
                    <option value="suspended">Đình chỉ</option>
                    <option value="graduated">Tốt nghiệp</option>
                  </select>
                  <span className="material-symbols-outlined absolute right-3 top-1/2 -translate-y-1/2 text-outline-variant pointer-events-none">expand_more</span>
                </div>
              </div>
            </div>
          </div>
          
          <div className="bg-surface-container-lowest rounded-xl shadow-[var(--shadow-float)] border border-border-muted p-lg">
            <button type="button" className="w-full flex justify-center items-center gap-2 py-2 border border-outline-variant rounded-lg text-on-surface hover:bg-surface-container transition-colors text-button">
              <span className="material-symbols-outlined text-[20px]">lock_reset</span>
              Gửi email Reset Mật khẩu
            </button>
          </div>
        </div>

        {/* Action Buttons */}
        <div className="lg:col-span-3 flex justify-end gap-3 pt-6 border-t border-border-muted">
          <Link href="/users" className="px-6 py-2.5 rounded-lg border border-outline-variant hover:bg-surface-container transition-colors text-button text-on-surface">Hủy bỏ</Link>
          <button type="submit" disabled={isSaving} className="px-6 py-2.5 rounded-lg bg-primary text-on-primary hover:bg-primary/90 transition-colors text-button flex items-center gap-2 disabled:opacity-70">
            {isSaving ? <span className="material-symbols-outlined animate-spin text-[20px]">progress_activity</span> : <span className="material-symbols-outlined text-[20px]">save</span>}
            Lưu thay đổi
          </button>
        </div>
      </form>
    </main>
  );
}
