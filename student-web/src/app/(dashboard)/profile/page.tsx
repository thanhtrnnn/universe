"use client";

import React from "react";
import { useAuthStore } from "@/store/authStore";

export default function ProfilePage() {
  const { user } = useAuthStore();

  return (
    <main className="flex-1 overflow-y-auto mt-16 p-xl bg-background">
      <div className="mb-lg">
        <h1 className="text-h1 text-on-surface">Hồ sơ cá nhân</h1>
        <p className="text-body-md text-on-surface-variant mt-1">
          Thông tin chi tiết về sinh viên
        </p>
      </div>

      <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
        {/* Left column: Avatar and basic info */}
        <div className="col-span-1">
          <div className="bg-surface rounded-2xl p-6 border border-border-muted shadow-sm flex flex-col items-center text-center">
            <div className="relative mb-4">
              <img 
                src="https://i.pravatar.cc/150?img=12" 
                alt="Avatar" 
                className="w-32 h-32 rounded-full object-cover border-4 border-surface shadow-sm"
              />
              <button className="absolute bottom-0 right-0 p-2 bg-primary text-on-primary rounded-full shadow-sm hover:brightness-110 transition-all">
                <span className="material-symbols-outlined text-[18px]">edit</span>
              </button>
            </div>
            
            <h2 className="text-h2 text-on-surface mb-1">{user?.name || "Phạm Thị Thiên Hà"}</h2>
            <p className="text-body-md font-semibold text-primary mb-1">{user?.code || "B23DCCN266"}</p>
            <p className="text-body-sm text-on-surface-variant mb-4">Lớp: D23CQCE01-B</p>
            
            <div className="w-full flex gap-2">
              <button className="flex-1 bg-primary text-on-primary py-2 rounded-lg font-semibold hover:bg-primary/90 transition-colors">
                Cập nhật
              </button>
              <button className="flex-1 bg-surface-container border border-outline-variant py-2 rounded-lg font-semibold hover:bg-surface-container-high transition-colors">
                Đổi mật khẩu
              </button>
            </div>
          </div>
        </div>

        {/* Right column: Detailed info */}
        <div className="col-span-1 md:col-span-2">
          <div className="bg-surface rounded-2xl p-6 border border-border-muted shadow-sm h-full">
            <h3 className="text-body-lg font-semibold text-on-surface mb-6 pb-2 border-b border-border-muted">Thông tin học thuật</h3>
            
            <div className="grid grid-cols-1 sm:grid-cols-2 gap-6 mb-8">
              <div>
                <p className="text-body-sm text-on-surface-variant mb-1">Khoa / Viện</p>
                <p className="text-body-md font-medium text-on-surface">Công nghệ thông tin 1</p>
              </div>
              <div>
                <p className="text-body-sm text-on-surface-variant mb-1">Ngành học</p>
                <p className="text-body-md font-medium text-on-surface">Kỹ thuật máy tính</p>
              </div>
              <div>
                <p className="text-body-sm text-on-surface-variant mb-1">Hệ đào tạo</p>
                <p className="text-body-md font-medium text-on-surface">Đại học chính quy</p>
              </div>
              <div>
                <p className="text-body-sm text-on-surface-variant mb-1">Khóa học</p>
                <p className="text-body-md font-medium text-on-surface">2023 - 2027</p>
              </div>
              <div>
                <p className="text-body-sm text-on-surface-variant mb-1">Tình trạng học tập</p>
                <p className="inline-block px-3 py-1 bg-green-500/10 text-green-700 rounded-full text-xs font-semibold">Đang học</p>
              </div>
              <div>
                <p className="text-body-sm text-on-surface-variant mb-1">Giảng viên cố vấn</p>
                <p className="text-body-md font-medium text-on-surface">Đỗ Thị Liên</p>
              </div>
            </div>

            <h3 className="text-body-lg font-semibold text-on-surface mb-6 pb-2 border-b border-border-muted">Thông tin liên lạc</h3>
            
            <div className="grid grid-cols-1 sm:grid-cols-2 gap-6">
              <div>
                <p className="text-body-sm text-on-surface-variant mb-1">Email trường</p>
                <p className="text-body-md font-medium text-on-surface">thienhapt266@stu.ptit.edu.vn</p>
              </div>
              <div>
                <p className="text-body-sm text-on-surface-variant mb-1">Email cá nhân</p>
                <p className="text-body-md font-medium text-on-surface">thienha.pt@gmail.com</p>
              </div>
              <div>
                <p className="text-body-sm text-on-surface-variant mb-1">Số điện thoại</p>
                <p className="text-body-md font-medium text-on-surface">0987 654 321</p>
              </div>
              <div>
                <p className="text-body-sm text-on-surface-variant mb-1">Ngày sinh</p>
                <p className="text-body-md font-medium text-on-surface">15/08/2005</p>
              </div>
            </div>
          </div>
        </div>
      </div>
    </main>
  );
}
