"use client";

import React, { useState } from "react";

export default function LecturerProfilePage() {
  const [isEditing, setIsEditing] = useState(false);

  // Mock data for lecturer profile
  const [profile, setProfile] = useState({
    name: "TS. Nguyễn Văn A",
    lecturerId: "D05CN012",
    email: "nguyenvana@universe.edu.vn",
    phone: "0987654321",
    department: "Khoa Công nghệ Thông tin",
    faculty: "Kỹ thuật Phần mềm",
    degree: "Tiến sĩ",
    joinDate: "2015-08-15",
    bio: "Giảng viên xuất sắc có nhiều năm kinh nghiệm trong lĩnh vực Phát triển phần mềm và Cấu trúc dữ liệu.",
  });

  const handleChange = (e: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement>) => {
    const { name, value } = e.target;
    setProfile(prev => ({ ...prev, [name]: value }));
  };

  const handleSave = () => {
    setIsEditing(false);
    // Here we would typically save to the backend
  };

  return (
    <main className="flex-1 overflow-y-auto p-4 md:p-8 bg-background min-h-[calc(100vh-4rem)]">
      <div className="max-w-4xl mx-auto space-y-6">
        
        {/* Header Section */}
        <div className="flex flex-col md:flex-row justify-between items-start md:items-center gap-4">
          <div>
            <h2 className="font-h1 text-h2 text-on-background tracking-tight">Hồ sơ Giảng viên</h2>
            <p className="text-body-md text-on-surface-variant mt-1">Quản lý thông tin cá nhân và tài khoản</p>
          </div>
          <div className="flex items-center gap-3">
            {!isEditing ? (
              <button
                onClick={() => setIsEditing(true)}
                className="flex items-center gap-2 font-button text-button px-4 py-2 bg-primary text-on-primary hover:bg-primary/90 rounded-lg transition-colors shadow-sm"
              >
                <span className="material-symbols-outlined text-[18px]">edit</span>
                Chỉnh sửa
              </button>
            ) : (
              <>
                <button
                  onClick={() => setIsEditing(false)}
                  className="flex items-center gap-2 font-button text-button px-4 py-2 border border-outline-variant text-on-surface hover:bg-surface-container-low rounded-lg transition-colors"
                >
                  Hủy
                </button>
                <button
                  onClick={handleSave}
                  className="flex items-center gap-2 font-button text-button px-4 py-2 bg-primary text-on-primary hover:bg-primary/90 rounded-lg transition-colors shadow-sm"
                >
                  <span className="material-symbols-outlined text-[18px]">save</span>
                  Lưu thay đổi
                </button>
              </>
            )}
          </div>
        </div>

        <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
          {/* Avatar and Basic Info Card */}
          <div className="md:col-span-1 bg-surface-container-lowest rounded-xl shadow-[var(--shadow-card)] border border-outline-variant/30 overflow-hidden flex flex-col">
            <div className="h-32 bg-gradient-to-r from-primary/80 to-tertiary/80 w-full relative" />
            <div className="px-6 pb-6 flex flex-col items-center relative -mt-16">
              <div className="w-32 h-32 rounded-full border-4 border-surface-container-lowest bg-surface-container-high overflow-hidden flex items-center justify-center relative shadow-sm">
                <span className="material-symbols-outlined text-[64px] text-on-surface-variant">person</span>
                {isEditing && (
                  <button className="absolute inset-0 bg-black/40 flex items-center justify-center text-white opacity-0 hover:opacity-100 transition-opacity">
                    <span className="material-symbols-outlined">photo_camera</span>
                  </button>
                )}
              </div>
              <h3 className="text-h3 font-semibold text-on-surface mt-4 text-center">{profile.name}</h3>
              <p className="text-body-sm text-on-surface-variant mt-1">Mã GV: <span className="font-semibold text-primary">{profile.lecturerId}</span></p>
              <p className="text-body-sm text-primary font-medium mt-1">{profile.department}</p>
              
              <div className="w-full mt-6 space-y-4 pt-6 border-t border-surface-variant">
                <div className="flex items-center gap-3">
                  <div className="w-8 h-8 rounded-full bg-primary-container/30 flex items-center justify-center text-primary">
                    <span className="material-symbols-outlined text-[18px]">badge</span>
                  </div>
                  <div>
                    <p className="text-label text-on-surface-variant">Chức danh</p>
                    <p className="text-body-sm font-medium text-on-surface">{profile.degree}</p>
                  </div>
                </div>
                <div className="flex items-center gap-3">
                  <div className="w-8 h-8 rounded-full bg-secondary-container/30 flex items-center justify-center text-secondary">
                    <span className="material-symbols-outlined text-[18px]">calendar_month</span>
                  </div>
                  <div>
                    <p className="text-label text-on-surface-variant">Ngày tham gia</p>
                    <p className="text-body-sm font-medium text-on-surface">15/08/2015</p>
                  </div>
                </div>
              </div>
            </div>
          </div>

          {/* Detailed Info Form */}
          <div className="md:col-span-2 bg-surface-container-lowest rounded-xl shadow-[var(--shadow-card)] border border-outline-variant/30 p-6">
            <h3 className="text-h3 font-semibold text-on-surface mb-6 flex items-center gap-2">
              <span className="material-symbols-outlined text-primary">contact_page</span>
              Thông tin chi tiết
            </h3>
            
            <div className="space-y-5">
              <div className="grid grid-cols-1 md:grid-cols-2 gap-5">
                <div>
                  <label className="block text-label text-on-surface-variant mb-1">Mã giảng viên</label>
                  {isEditing ? (
                    <input
                      type="text"
                      name="lecturerId"
                      value={profile.lecturerId}
                      onChange={handleChange}
                      className="w-full px-3 py-2 bg-surface-container-lowest border border-outline rounded-lg focus:outline-none focus:border-primary text-body-md"
                    />
                  ) : (
                    <div className="px-3 py-2 bg-surface-container-low border border-transparent rounded-lg text-body-md text-on-surface font-mono">
                      {profile.lecturerId}
                    </div>
                  )}
                </div>
                <div>
                  <label className="block text-label text-on-surface-variant mb-1">Họ và tên</label>
                  {isEditing ? (
                    <input
                      type="text"
                      name="name"
                      value={profile.name}
                      onChange={handleChange}
                      className="w-full px-3 py-2 bg-surface-container-lowest border border-outline rounded-lg focus:outline-none focus:border-primary text-body-md"
                    />
                  ) : (
                    <div className="px-3 py-2 bg-surface-container-low border border-transparent rounded-lg text-body-md text-on-surface">
                      {profile.name}
                    </div>
                  )}
                </div>
                <div>
                  <label className="block text-label text-on-surface-variant mb-1">Học vị / Chức danh</label>
                  {isEditing ? (
                    <input
                      type="text"
                      name="degree"
                      value={profile.degree}
                      onChange={handleChange}
                      className="w-full px-3 py-2 bg-surface-container-lowest border border-outline rounded-lg focus:outline-none focus:border-primary text-body-md"
                    />
                  ) : (
                    <div className="px-3 py-2 bg-surface-container-low border border-transparent rounded-lg text-body-md text-on-surface">
                      {profile.degree}
                    </div>
                  )}
                </div>
              </div>

              <div className="grid grid-cols-1 md:grid-cols-2 gap-5">
                <div>
                  <label className="block text-label text-on-surface-variant mb-1">Email</label>
                  {isEditing ? (
                    <input
                      type="email"
                      name="email"
                      value={profile.email}
                      onChange={handleChange}
                      className="w-full px-3 py-2 bg-surface-container-lowest border border-outline rounded-lg focus:outline-none focus:border-primary text-body-md"
                    />
                  ) : (
                    <div className="px-3 py-2 bg-surface-container-low border border-transparent rounded-lg text-body-md text-on-surface">
                      {profile.email}
                    </div>
                  )}
                </div>
                <div>
                  <label className="block text-label text-on-surface-variant mb-1">Số điện thoại</label>
                  {isEditing ? (
                    <input
                      type="tel"
                      name="phone"
                      value={profile.phone}
                      onChange={handleChange}
                      className="w-full px-3 py-2 bg-surface-container-lowest border border-outline rounded-lg focus:outline-none focus:border-primary text-body-md"
                    />
                  ) : (
                    <div className="px-3 py-2 bg-surface-container-low border border-transparent rounded-lg text-body-md text-on-surface">
                      {profile.phone}
                    </div>
                  )}
                </div>
              </div>

              <div className="grid grid-cols-1 md:grid-cols-2 gap-5">
                <div>
                  <label className="block text-label text-on-surface-variant mb-1">Khoa / Đơn vị quản lý</label>
                  {isEditing ? (
                    <input
                      type="text"
                      name="department"
                      value={profile.department}
                      onChange={handleChange}
                      className="w-full px-3 py-2 bg-surface-container-lowest border border-outline rounded-lg focus:outline-none focus:border-primary text-body-md"
                    />
                  ) : (
                    <div className="px-3 py-2 bg-surface-container-low border border-transparent rounded-lg text-body-md text-on-surface">
                      {profile.department}
                    </div>
                  )}
                </div>
                <div>
                  <label className="block text-label text-on-surface-variant mb-1">Bộ môn</label>
                  {isEditing ? (
                    <input
                      type="text"
                      name="faculty"
                      value={profile.faculty}
                      onChange={handleChange}
                      className="w-full px-3 py-2 bg-surface-container-lowest border border-outline rounded-lg focus:outline-none focus:border-primary text-body-md"
                    />
                  ) : (
                    <div className="px-3 py-2 bg-surface-container-low border border-transparent rounded-lg text-body-md text-on-surface">
                      {profile.faculty}
                    </div>
                  )}
                </div>
              </div>

              <div>
                <label className="block text-label text-on-surface-variant mb-1">Tiểu sử & Chuyên môn</label>
                {isEditing ? (
                  <textarea
                    name="bio"
                    value={profile.bio}
                    onChange={handleChange}
                    rows={4}
                    className="w-full px-3 py-2 bg-surface-container-lowest border border-outline rounded-lg focus:outline-none focus:border-primary text-body-md resize-none"
                  />
                ) : (
                  <div className="px-3 py-3 bg-surface-container-low border border-transparent rounded-lg text-body-md text-on-surface whitespace-pre-wrap min-h-[100px]">
                    {profile.bio}
                  </div>
                )}
              </div>
            </div>
            
            {/* Security Settings Section (Read Only Mock for now) */}
            <div className="mt-8 pt-8 border-t border-surface-variant">
              <h4 className="text-h4 font-semibold text-on-surface mb-4">Bảo mật tài khoản</h4>
              <div className="flex items-center justify-between p-4 bg-surface-container-low rounded-lg">
                <div className="flex items-center gap-3">
                  <span className="material-symbols-outlined text-on-surface-variant">password</span>
                  <div>
                    <p className="text-body-sm font-semibold text-on-surface">Mật khẩu đăng nhập</p>
                    <p className="text-label text-on-surface-variant">Lần đổi gần nhất: 3 tháng trước</p>
                  </div>
                </div>
                <button className="text-button text-primary hover:text-primary/80 font-medium">Đổi mật khẩu</button>
              </div>
            </div>
            
          </div>
        </div>
      </div>
    </main>
  );
}
