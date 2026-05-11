"use client";

import React from "react";

export default function LecturerDashboard() {
  return (
    <main className="flex-1 pt-24 px-xl pb-xl bg-background overflow-y-auto">
      <div className="mb-lg">
        <h1 className="font-h1 text-h1 text-on-background">Tổng quan</h1>
        <p className="font-body-md text-body-md text-on-surface-variant mt-1">Chào ngày mới! Dưới đây là tổng hợp hoạt động giảng dạy của bạn.</p>
      </div>

      {/* Bento Grid */}
      <div className="grid grid-cols-12 gap-container_gutter mb-xl">
        {/* KPI Cards (Row 1) */}
        <div className="col-span-12 md:col-span-6 lg:col-span-3 bg-surface-container-lowest rounded-xl shadow-[0px_4px_20px_rgba(26,26,26,0.05)] p-lg flex flex-col justify-between border border-outline-variant/30">
          <div className="flex justify-between items-start mb-md">
            <div className="w-10 h-10 rounded-lg bg-primary-fixed flex items-center justify-center">
              <span className="material-symbols-outlined text-on-primary-fixed" style={{ fontVariationSettings: "'FILL' 1" }}>class</span>
            </div>
          </div>
          <div>
            <div className="font-h2 text-h2 text-on-surface mb-1">4</div>
            <div className="font-body-sm text-body-sm text-on-surface-variant">Lớp đang phụ trách</div>
          </div>
        </div>

        <div className="col-span-12 md:col-span-6 lg:col-span-3 bg-surface-container-lowest rounded-xl shadow-[0px_4px_20px_rgba(26,26,26,0.05)] p-lg flex flex-col justify-between border border-outline-variant/30 relative overflow-hidden">
          <div className="flex justify-between items-start mb-md relative z-10">
            <div className="w-10 h-10 rounded-lg bg-secondary-container flex items-center justify-center">
              <span className="material-symbols-outlined text-on-secondary-container" style={{ fontVariationSettings: "'FILL' 1" }}>how_to_reg</span>
            </div>
            <span className="font-label text-label text-secondary bg-secondary-fixed-dim/30 px-2 py-1 rounded-full">+2% tuần này</span>
          </div>
          <div className="relative z-10">
            <div className="font-h2 text-h2 text-on-surface mb-1">92%</div>
            <div className="font-body-sm text-body-sm text-on-surface-variant">Tỉ lệ chuyên cần tổng hợp</div>
          </div>
          <div className="absolute bottom-0 right-0 w-24 h-24 bg-secondary-container/20 rounded-tl-full blur-xl"></div>
        </div>

        <div className="col-span-12 md:col-span-6 lg:col-span-3 bg-surface-container-lowest rounded-xl shadow-[0px_4px_20px_rgba(26,26,26,0.05)] p-lg flex flex-col justify-between border border-outline-variant/30">
          <div className="flex justify-between items-start mb-md">
            <div className="w-10 h-10 rounded-lg bg-tertiary-fixed flex items-center justify-center">
              <span className="material-symbols-outlined text-on-tertiary-fixed" style={{ fontVariationSettings: "'FILL' 1" }}>calendar_today</span>
            </div>
          </div>
          <div>
            <div className="font-h2 text-h2 text-on-surface mb-1">2 ca</div>
            <div className="font-body-sm text-body-sm text-on-surface-variant">Lịch dạy hôm nay</div>
          </div>
        </div>

        <div className="col-span-12 md:col-span-6 lg:col-span-3 bg-surface-container-lowest rounded-xl shadow-[0px_4px_20px_rgba(26,26,26,0.05)] p-lg flex flex-col justify-between border border-error-container relative overflow-hidden">
          <div className="flex justify-between items-start mb-md relative z-10">
            <div className="w-10 h-10 rounded-lg bg-error-container flex items-center justify-center">
              <span className="material-symbols-outlined text-on-error-container" style={{ fontVariationSettings: "'FILL' 1" }}>event_busy</span>
            </div>
            <span className="w-3 h-3 rounded-full bg-error animate-pulse"></span>
          </div>
          <div className="relative z-10">
            <div className="font-h2 text-h2 text-error mb-1">5</div>
            <div className="font-body-sm text-body-sm text-on-surface-variant">Đơn vắng phép chờ duyệt</div>
          </div>
          <div className="absolute top-0 right-0 w-32 h-32 bg-error-container/30 rounded-bl-full blur-2xl"></div>
        </div>
      </div>

      <div className="grid grid-cols-12 gap-container_gutter">
        {/* Today's Schedule */}
        <div className="col-span-12 lg:col-span-8 bg-surface-container-lowest rounded-xl shadow-[0px_4px_20px_rgba(26,26,26,0.05)] border border-outline-variant/30 flex flex-col overflow-hidden">
          <div className="h-32 w-full bg-cover bg-center relative" style={{ backgroundImage: "url('https://lh3.googleusercontent.com/aida-public/AB6AXuAiRbhrQH9gxSjZ1IVtgw62g0ZwAuVY6WzXtfRtfBQak8142GIJrmikPVfiPXf4cxahV0RPjN6O_STosZnuUh480PtCvzKGq6o0J7sxzsujOPR8LFbAxurmh8M3n_N5aH1NXaGQkm0mdvM52sefUh4IXeiwWqLCt8Jg0uY0jcWhWNQffwunBcSr1Rl1Fer5O7o-NmPYLz7RRFm2a0-vJUUefWsVhUR9s8RSMb-fziDbr2YOHW8JRdwD_C6i1Gk2or4iXVtuQwCSK8c')" }}>
            <div className="absolute inset-0 bg-gradient-to-t from-surface-container-lowest to-transparent"></div>
            <div className="absolute bottom-md left-lg">
              <h3 className="font-h3 text-h3 text-on-surface flex items-center gap-2">
                <span className="material-symbols-outlined text-primary">schedule</span> Lịch dạy hôm nay
              </h3>
            </div>
          </div>
          <div className="p-lg flex-1 flex flex-col gap-4">
            <div className="flex flex-col sm:flex-row gap-4 items-start sm:items-center justify-between p-4 rounded-lg bg-surface hover:bg-surface-container-low transition-colors border border-transparent hover:border-outline-variant/50">
              <div className="flex items-start gap-4">
                <div className="flex flex-col items-center justify-center w-16 h-16 rounded-lg bg-primary-fixed text-on-primary-fixed">
                  <span className="font-h3 text-h3">07:30</span>
                </div>
                <div>
                  <h4 className="font-button text-button text-on-surface">Lập trình Web nâng cao</h4>
                  <p className="font-body-sm text-body-sm text-on-surface-variant flex items-center gap-1 mt-1">
                    <span className="material-symbols-outlined text-[16px]">location_on</span> Phòng A3-102 • Nhóm 01
                  </p>
                </div>
              </div>
              <button className="font-button text-button text-on-primary bg-primary px-4 py-2 rounded-lg hover:bg-primary/90 transition-colors whitespace-nowrap self-stretch sm:self-auto flex items-center justify-center gap-2 shadow-sm">
                <span className="material-symbols-outlined text-[18px]">how_to_reg</span> Điểm danh
              </button>
            </div>

            <div className="flex flex-col sm:flex-row gap-4 items-start sm:items-center justify-between p-4 rounded-lg bg-surface hover:bg-surface-container-low transition-colors border border-transparent hover:border-outline-variant/50">
              <div className="flex items-start gap-4">
                <div className="flex flex-col items-center justify-center w-16 h-16 rounded-lg bg-surface-variant text-on-surface-variant">
                  <span className="font-h3 text-h3">13:00</span>
                </div>
                <div>
                  <h4 className="font-button text-button text-on-surface">Cấu trúc dữ liệu & Thuật toán</h4>
                  <p className="font-body-sm text-body-sm text-on-surface-variant flex items-center gap-1 mt-1">
                    <span className="material-symbols-outlined text-[16px]">location_on</span> Phòng B1-205 • Nhóm 03
                  </p>
                </div>
              </div>
              <button className="font-button text-button text-primary bg-primary-fixed px-4 py-2 rounded-lg hover:bg-primary-fixed-dim transition-colors whitespace-nowrap self-stretch sm:self-auto flex items-center justify-center gap-2">
                Xem chi tiết
              </button>
            </div>
          </div>
        </div>

        {/* Attendance Warnings */}
        <div className="col-span-12 lg:col-span-4 bg-surface-container-lowest rounded-xl shadow-[0px_4px_20px_rgba(26,26,26,0.05)] p-lg border border-outline-variant/30 flex flex-col">
          <div className="flex items-center justify-between mb-lg">
            <h3 className="font-h3 text-h3 text-on-surface flex items-center gap-2">
              <span className="material-symbols-outlined text-error">warning</span> Cảnh báo chuyên cần
            </h3>
            <button className="font-label text-label text-primary hover:text-primary-container transition-colors">Xem tất cả</button>
          </div>
          <div className="flex flex-col gap-4">
            <div className="flex items-center justify-between p-3 rounded-lg bg-error-container/30 border border-error-container/50">
              <div className="flex items-center gap-3">
                <div className="w-10 h-10 rounded-full bg-surface-variant flex items-center justify-center text-on-surface-variant font-button text-button uppercase">NA</div>
                <div>
                  <div className="font-button text-button text-on-surface">Nguyễn Văn A</div>
                  <div className="font-body-sm text-body-sm text-on-surface-variant truncate w-32 md:w-40">Lập trình Web nâng cao</div>
                </div>
              </div>
              <span className="font-label text-label text-error bg-error-container px-2 py-0.5 rounded">75%</span>
            </div>

            <div className="flex items-center justify-between p-3 rounded-lg bg-error-container/30 border border-error-container/50">
              <div className="flex items-center gap-3">
                <div className="w-10 h-10 rounded-full bg-surface-variant flex items-center justify-center text-on-surface-variant font-button text-button uppercase">TB</div>
                <div>
                  <div className="font-button text-button text-on-surface">Trần Thị B</div>
                  <div className="font-body-sm text-body-sm text-on-surface-variant truncate w-32 md:w-40">CTDL & Thuật toán</div>
                </div>
              </div>
              <span className="font-label text-label text-error bg-error-container px-2 py-0.5 rounded">68%</span>
            </div>

            <div className="flex items-center justify-between p-3 rounded-lg bg-surface hover:bg-surface-container-low border border-transparent hover:border-outline-variant/50 transition-colors">
              <div className="flex items-center gap-3">
                <div className="w-10 h-10 rounded-full bg-surface-variant flex items-center justify-center text-on-surface-variant font-button text-button uppercase">LM</div>
                <div>
                  <div className="font-button text-button text-on-surface">Lê Hoàng M</div>
                  <div className="font-body-sm text-body-sm text-on-surface-variant truncate w-32 md:w-40">Lập trình Web nâng cao</div>
                </div>
              </div>
              <span className="font-label text-label text-secondary bg-secondary-container px-2 py-0.5 rounded">82%</span>
            </div>
          </div>
        </div>
      </div>
    </main>
  );
}

