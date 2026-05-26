"use client";

import React, { useState } from "react";

export default function StatisticsPage() {
  const kpiStats = [
    { label: "TỔNG SINH VIÊN", value: "12,450", sub: "+2.4% so với kỳ trước", subIcon: "trending_up", subColor: "text-secondary", bg: "bg-primary/5" },
    { label: "TỶ LỆ ĐI HỌC", value: "94.2%", sub: null, bar: true, barPercent: 94.2, bg: "bg-secondary/5" },
    { label: "ĐI TRẺ TRUNG BÌNH", value: "3.5%", sub: "~435 lượt/ngày", subIcon: "schedule", subColor: "text-on-surface-variant", bg: "bg-tertiary/5" },
    { label: "TỶ LỆ VẮNG MẶT", value: "2.3%", sub: "-0.5% so với tháng trước", subIcon: "trending_down", subColor: "text-error", bg: "bg-error/5" },
  ];

  const barData = [
    { label: "CNTT", value: 92 },
    { label: "Kinh tế", value: 88 },
    { label: "Ngoại ngữ", value: 85 },
    { label: "Du lịch", value: 78 },
    { label: "Y Dược", value: 95 },
  ];

  const [isExporting, setIsExporting] = useState(false);

  const handleExport = (type: "pdf" | "excel") => {
    setIsExporting(true);
    setTimeout(() => {
      setIsExporting(false);
      alert(`Đã xuất báo cáo dưới định dạng ${type.toUpperCase()} thành công!`);
    }, 1500);
  };

  return (
    <main className="flex-1 overflow-y-auto mt-16 p-xl bg-background">
      {/* Header */}
      <div className="flex justify-between items-start mb-lg">
        <div>
          <h2 className="text-h1 text-on-surface">Thống kê & Báo cáo</h2>
          <p className="text-body-md text-on-surface-variant mt-1">
            Tổng quan về tình hình học tập và điểm danh.
          </p>
        </div>
        <div className="flex gap-2">
          <button 
            onClick={() => handleExport("pdf")}
            disabled={isExporting}
            className="flex items-center gap-2 px-4 py-2 border border-outline-variant rounded-lg text-button text-on-surface hover:bg-surface-container transition-colors bg-surface-container-lowest disabled:opacity-50"
          >
            <span className="material-symbols-outlined text-[18px]">picture_as_pdf</span>
            Xuất PDF
          </button>
          <button 
            onClick={() => handleExport("excel")}
            disabled={isExporting}
            className="flex items-center gap-2 px-4 py-2 bg-primary text-on-primary rounded-lg text-button hover:bg-on-primary-fixed-variant transition-colors shadow-sm disabled:opacity-50"
          >
            <span className="material-symbols-outlined text-[18px]">grid_on</span>
            Xuất Excel
          </button>
        </div>
      </div>

      {/* Filter Bar */}
      <div className="bg-surface-container-lowest rounded-xl p-md shadow-[var(--shadow-card)] border border-border-muted mb-container_gutter flex items-center gap-lg">
        <div className="flex items-center gap-2">
          <span className="material-symbols-outlined text-on-surface-variant text-[18px]">tune</span>
          <span className="text-label text-on-surface-variant uppercase">BỘ LỌC:</span>
        </div>
        <div className="flex gap-md flex-1">
          <div>
            <label className="text-label text-on-surface-variant block mb-1">Học kỳ</label>
            <select className="bg-surface-container-lowest border border-outline-variant rounded-lg px-4 py-2 text-body-md appearance-none pr-8 focus:outline-none focus:border-primary focus:ring-1 focus:ring-primary">
              <option>Học kỳ 1 - 2024/2025</option>
              <option>Học kỳ 2 - 2023/2024</option>
            </select>
          </div>
          <div>
            <label className="text-label text-on-surface-variant block mb-1">Thời gian</label>
            <input 
              type="month" 
              className="bg-surface-container-lowest border border-outline-variant rounded-lg px-4 py-2 text-body-md focus:outline-none focus:border-primary focus:ring-1 focus:ring-primary w-full"
            />
          </div>
          <div>
            <label className="text-label text-on-surface-variant block mb-1">Khoa / Viện</label>
            <select className="bg-surface-container-lowest border border-outline-variant rounded-lg px-4 py-2 text-body-md appearance-none pr-8 focus:outline-none focus:border-primary focus:ring-1 focus:ring-primary w-full">
              <option>Tất cả các khoa</option>
              <option>CNTT</option>
              <option>Kinh tế</option>
            </select>
          </div>
        </div>
      </div>

      {/* KPI Stats */}
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-container_gutter mb-container_gutter">
        {kpiStats.map((kpi, idx) => (
          <div key={idx} className={`${kpi.bg} rounded-xl p-lg border border-border-muted`}>
            <p className="text-label text-on-surface-variant uppercase mb-2">{kpi.label}</p>
            <h3 className="text-h1 text-on-surface mb-2">{kpi.value}</h3>
            {kpi.bar && (
              <div className="w-full bg-surface-container-high rounded-full h-2">
                <div className="bg-secondary h-2 rounded-full" style={{ width: `${kpi.barPercent}%` }}></div>
              </div>
            )}
            {kpi.sub && (
              <p className={`text-body-sm ${kpi.subColor} flex items-center gap-1 mt-1`}>
                <span className="material-symbols-outlined text-[16px]">{kpi.subIcon}</span>
                {kpi.sub}
              </p>
            )}
          </div>
        ))}
      </div>

      {/* Charts Row */}
      <div className="grid grid-cols-1 lg:grid-cols-3 gap-container_gutter mb-container_gutter">
        {/* Bar Chart */}
        <div className="lg:col-span-2 bg-surface-container-lowest rounded-xl p-lg shadow-[var(--shadow-card)] border border-border-muted">
          <div className="flex justify-between items-center mb-lg">
            <h3 className="text-h3 text-on-surface">Tỷ lệ chuyên cần theo Khoa</h3>
            <button className="p-2 text-on-surface-variant hover:bg-surface-container-highest rounded-lg transition-colors">
              <span className="material-symbols-outlined">more_vert</span>
            </button>
          </div>
          <div className="h-64 flex items-end justify-between gap-4 pt-8">
            {barData.map((item, idx) => (
              <div key={idx} className="flex flex-col items-center flex-1 gap-2 group">
                <div className="w-full bg-surface-container-high rounded-t-sm relative h-full flex items-end">
                  <div className="w-full bg-primary/80 group-hover:bg-primary rounded-t-sm transition-colors" style={{ height: `${item.value}%` }}></div>
                  <span className="absolute -top-6 left-1/2 -translate-x-1/2 text-label text-on-surface-variant opacity-0 group-hover:opacity-100 transition-opacity">{item.value}%</span>
                </div>
                <span className="text-label text-on-surface-variant">{item.label}</span>
              </div>
            ))}
          </div>
        </div>

        {/* Donut Chart */}
        <div className="bg-surface-container-lowest rounded-xl p-lg shadow-[var(--shadow-card)] border border-border-muted">
          <h3 className="text-h3 text-on-surface mb-lg">Phân bố trạng thái</h3>
          {/* Faux Donut */}
          <div className="flex justify-center mb-lg">
            <div className="relative w-48 h-48">
              <svg viewBox="0 0 200 200" className="w-full h-full -rotate-90">
                <circle cx="100" cy="100" r="80" fill="none" stroke="var(--surface-container-high)" strokeWidth="24" />
                <circle cx="100" cy="100" r="80" fill="none" stroke="var(--secondary)" strokeWidth="24" strokeDasharray="377" strokeDashoffset="94" strokeLinecap="round" />
                <circle cx="100" cy="100" r="80" fill="none" stroke="#F59E0B" strokeWidth="24" strokeDasharray="377" strokeDashoffset="320" strokeLinecap="round" />
                <circle cx="100" cy="100" r="80" fill="none" stroke="var(--error)" strokeWidth="24" strokeDasharray="377" strokeDashoffset="340" strokeLinecap="round" />
              </svg>
              <div className="absolute inset-0 flex flex-col items-center justify-center">
                <span className="text-h1 text-on-surface">12.4k</span>
                <span className="text-body-sm text-on-surface-variant">Lượt</span>
              </div>
            </div>
          </div>
          <div className="space-y-3">
            <div className="flex items-center justify-between">
              <div className="flex items-center gap-2">
                <div className="w-3 h-3 rounded-full bg-secondary"></div>
                <span className="text-body-sm text-on-surface">Có mặt (Đúng giờ)</span>
              </div>
              <span className="text-button text-on-surface">75%</span>
            </div>
            <div className="flex items-center justify-between">
              <div className="flex items-center gap-2">
                <div className="w-3 h-3 rounded-full bg-amber-500"></div>
                <span className="text-body-sm text-on-surface">Đi trễ</span>
              </div>
              <span className="text-button text-on-surface">15%</span>
            </div>
            <div className="flex items-center justify-between">
              <div className="flex items-center gap-2">
                <div className="w-3 h-3 rounded-full bg-error"></div>
                <span className="text-body-sm text-on-surface">Vắng mặt</span>
              </div>
              <span className="text-button text-on-surface">10%</span>
            </div>
          </div>
        </div>
      </div>

      {/* Trend Section */}
      <div className="bg-surface-container-lowest rounded-xl p-lg shadow-[var(--shadow-card)] border border-border-muted">
        <div className="flex justify-between items-center mb-lg">
          <h3 className="text-h3 text-on-surface">Xu hướng chuyên cần trong kỳ</h3>
          <div className="flex gap-2">
            <button className="px-3 py-1 rounded-full border border-outline-variant text-body-sm text-on-surface-variant hover:bg-surface-container transition-colors">Theo tuần</button>
            <button className="px-3 py-1 rounded-full bg-primary text-on-primary text-body-sm">Theo tháng</button>
          </div>
        </div>
        <div className="h-32 bg-primary/5 rounded-lg flex items-center justify-center">
          <p className="text-body-sm text-on-surface-variant">Biểu đồ xu hướng (placeholder)</p>
        </div>
      </div>
    </main>
  );
}
