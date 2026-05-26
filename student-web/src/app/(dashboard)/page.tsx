"use client";

import React from "react";
import Link from "next/link";
import { useAuthStore } from "@/store/authStore";

export default function StudentDashboard() {
  const { user } = useAuthStore();
  
  return (
    <main className="flex-1 pt-24 px-xl pb-xl bg-background overflow-y-auto">
      <div className="mb-lg">
        <h1 className="font-h1 text-h1 text-on-background">Tổng quan</h1>
        <p className="font-body-md text-body-md text-on-surface-variant mt-1">Chào ngày mới, {user?.name || "Sinh viên"}! Dưới đây là thông tin học tập của bạn.</p>
      </div>

      {/* Bento Grid */}
      <div className="grid grid-cols-12 gap-container_gutter mb-xl">
        {/* KPI Cards (Row 1) */}
        <div className="col-span-12 md:col-span-6 lg:col-span-3 bg-surface-container-lowest rounded-xl shadow-[0px_4px_20px_rgba(26,26,26,0.05)] p-lg flex flex-col justify-between border border-outline-variant/30">
          <div className="flex justify-between items-start mb-md">
            <div className="w-10 h-10 rounded-lg bg-primary-fixed flex items-center justify-center">
              <span className="material-symbols-outlined text-on-primary-fixed" style={{ fontVariationSettings: "'FILL' 1" }}>school</span>
            </div>
          </div>
          <div>
            <div className="font-h2 text-h2 text-on-surface mb-1">3.12</div>
            <div className="font-body-sm text-body-sm text-on-surface-variant">Điểm trung bình (GPA)</div>
          </div>
        </div>

        <div className="col-span-12 md:col-span-6 lg:col-span-3 bg-surface-container-lowest rounded-xl shadow-[0px_4px_20px_rgba(26,26,26,0.05)] p-lg flex flex-col justify-between border border-outline-variant/30 relative overflow-hidden">
          <div className="flex justify-between items-start mb-md relative z-10">
            <div className="w-10 h-10 rounded-lg bg-secondary-container flex items-center justify-center">
              <span className="material-symbols-outlined text-on-secondary-container" style={{ fontVariationSettings: "'FILL' 1" }}>local_library</span>
            </div>
          </div>
          <div className="relative z-10">
            <div className="font-h2 text-h2 text-on-surface mb-1">45/120</div>
            <div className="font-body-sm text-body-sm text-on-surface-variant">Tín chỉ tích lũy</div>
          </div>
          <div className="absolute bottom-0 right-0 w-24 h-24 bg-secondary-container/20 rounded-tl-full blur-xl"></div>
        </div>

        <div className="col-span-12 md:col-span-6 lg:col-span-3 bg-surface-container-lowest rounded-xl shadow-[0px_4px_20px_rgba(26,26,26,0.05)] p-lg flex flex-col justify-between border border-outline-variant/30">
          <div className="flex justify-between items-start mb-md">
            <div className="w-10 h-10 rounded-lg bg-tertiary-fixed flex items-center justify-center">
              <span className="material-symbols-outlined text-on-tertiary-fixed" style={{ fontVariationSettings: "'FILL' 1" }}>class</span>
            </div>
          </div>
          <div>
            <div className="font-h2 text-h2 text-on-surface mb-1">6</div>
            <div className="font-body-sm text-body-sm text-on-surface-variant">Môn học kỳ này</div>
          </div>
        </div>

        <div className="col-span-12 md:col-span-6 lg:col-span-3 bg-surface-container-lowest rounded-xl shadow-[0px_4px_20px_rgba(26,26,26,0.05)] p-lg flex flex-col justify-between border border-error-container relative overflow-hidden">
          <div className="flex justify-between items-start mb-md relative z-10">
            <div className="w-10 h-10 rounded-lg bg-error-container flex items-center justify-center">
              <span className="material-symbols-outlined text-on-error-container" style={{ fontVariationSettings: "'FILL' 1" }}>payments</span>
            </div>
            <span className="w-3 h-3 rounded-full bg-error animate-pulse"></span>
          </div>
          <div className="relative z-10">
            <div className="font-h2 text-h2 text-error mb-1">Chưa nộp</div>
            <div className="font-body-sm text-body-sm text-on-surface-variant">Học phí kỳ 2 (25-26)</div>
          </div>
          <div className="absolute top-0 right-0 w-32 h-32 bg-error-container/30 rounded-bl-full blur-2xl"></div>
        </div>
      </div>

      <div className="grid grid-cols-12 gap-container_gutter">
        {/* Today's Schedule */}
        <div className="col-span-12 lg:col-span-8 bg-surface-container-lowest rounded-xl shadow-[0px_4px_20px_rgba(26,26,26,0.05)] border border-outline-variant/30 flex flex-col overflow-hidden">
          <div className="h-32 w-full bg-cover bg-center relative" style={{ backgroundImage: "url('https://images.unsplash.com/photo-1541339907198-e08756dedf3f?q=80&w=2070&auto=format&fit=crop')" }}>
            <div className="absolute inset-0 bg-gradient-to-t from-surface-container-lowest to-transparent"></div>
            <div className="absolute bottom-md left-lg">
              <h3 className="font-h3 text-h3 text-on-surface flex items-center gap-2">
                <span className="material-symbols-outlined text-primary">schedule</span> Lịch học hôm nay
              </h3>
            </div>
          </div>
          <div className="p-lg flex-1 flex flex-col gap-4">
            <div className="flex flex-col sm:flex-row gap-4 items-start sm:items-center justify-between p-4 rounded-lg bg-surface hover:bg-surface-container-low transition-colors border border-transparent hover:border-outline-variant/50">
              <div className="flex items-start gap-4">
                <div className="flex flex-col items-center justify-center w-16 h-16 rounded-lg bg-primary-fixed text-on-primary-fixed">
                  <span className="font-h3 text-h3">07:00</span>
                </div>
                <div>
                  <h4 className="font-button text-button text-on-surface">Nhập môn CNPM</h4>
                  <p className="font-body-sm text-body-sm text-on-surface-variant flex items-center gap-1 mt-1">
                    <span className="material-symbols-outlined text-[16px]">location_on</span> Phòng 503-A1 • Đỗ Thị Liên
                  </p>
                </div>
              </div>
              <Link href="/attendance">
                <button className="font-button text-button text-on-primary bg-primary px-4 py-2 rounded-lg hover:bg-primary/90 transition-colors whitespace-nowrap self-stretch sm:self-auto flex items-center justify-center gap-2 shadow-sm">
                  <span className="material-symbols-outlined text-[18px]">qr_code_scanner</span> Điểm danh
                </button>
              </Link>
            </div>

            <div className="flex flex-col sm:flex-row gap-4 items-start sm:items-center justify-between p-4 rounded-lg bg-surface hover:bg-surface-container-low transition-colors border border-transparent hover:border-outline-variant/50">
              <div className="flex items-start gap-4">
                <div className="flex flex-col items-center justify-center w-16 h-16 rounded-lg bg-surface-variant text-on-surface-variant">
                  <span className="font-h3 text-h3">13:30</span>
                </div>
                <div>
                  <h4 className="font-button text-button text-on-surface">Thực tập cơ sở</h4>
                  <p className="font-body-sm text-body-sm text-on-surface-variant flex items-center gap-1 mt-1">
                    <span className="material-symbols-outlined text-[16px]">location_on</span> Phòng 304-A3 • P.T.Khánh
                  </p>
                </div>
              </div>
            </div>
          </div>
        </div>

        {/* Notifications */}
        <div className="col-span-12 lg:col-span-4 bg-surface-container-lowest rounded-xl shadow-[0px_4px_20px_rgba(26,26,26,0.05)] p-lg border border-outline-variant/30 flex flex-col">
          <div className="flex items-center justify-between mb-lg">
            <h3 className="font-h3 text-h3 text-on-surface flex items-center gap-2">
              <span className="material-symbols-outlined text-primary">campaign</span> Thông báo mới
            </h3>
            <Link href="/notifications" className="font-label text-label text-primary hover:text-primary-container transition-colors">Xem tất cả</Link>
          </div>
          <div className="flex flex-col gap-4">
            <div className="p-3 rounded-lg bg-surface hover:bg-surface-container-low border border-transparent hover:border-outline-variant/50 transition-colors">
              <div className="font-button text-button text-on-surface mb-1 line-clamp-1">Kế hoạch đăng ký môn học kỳ 1 năm học 2026-2027</div>
              <div className="font-body-sm text-body-sm text-on-surface-variant flex items-center gap-2">
                <span className="material-symbols-outlined text-[14px]">schedule</span> 26/05/2026
              </div>
            </div>

            <div className="p-3 rounded-lg bg-surface hover:bg-surface-container-low border border-transparent hover:border-outline-variant/50 transition-colors">
              <div className="font-button text-button text-on-surface mb-1 line-clamp-1">Thông báo nộp học phí kỳ 2 năm học 2025-2026</div>
              <div className="font-body-sm text-body-sm text-on-surface-variant flex items-center gap-2">
                <span className="material-symbols-outlined text-[14px]">schedule</span> 20/05/2026
              </div>
            </div>
            
            <div className="p-3 rounded-lg bg-surface hover:bg-surface-container-low border border-transparent hover:border-outline-variant/50 transition-colors">
              <div className="font-button text-button text-on-surface mb-1 line-clamp-1">Thông báo nghỉ lễ 30/4 và 1/5</div>
              <div className="font-body-sm text-body-sm text-on-surface-variant flex items-center gap-2">
                <span className="material-symbols-outlined text-[14px]">schedule</span> 15/04/2026
              </div>
            </div>
          </div>
        </div>
      </div>
    </main>
  );
}
