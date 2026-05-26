"use client";

import React from "react";
import Link from "next/link";

export default function DashboardPage() {
  const kpiData = [
    { label: "Tổng sinh viên", value: "12,450", icon: "groups", trend: "+5.2%", trendText: "so với tháng trước", color: "primary", up: true },
    { label: "Tổng giảng viên", value: "842", icon: "badge", trend: "+1.1%", trendText: "so với tháng trước", color: "tertiary", up: true },
    { label: "Lớp đang hoạt động", value: "456", icon: "class", trend: "→", trendText: "Không đổi", color: "secondary", up: false },
  ];

  const chartData = [
    { label: "CNTT", value: 85 },
    { label: "Kinh Tế", value: 92 },
    { label: "Ngoại Ngữ", value: 78 },
    { label: "Cơ Điện", value: 88 },
    { label: "Y Dược", value: 96, highlighted: true },
    { label: "Luật", value: 82 },
  ];

  const tableData = [
    { id: "CS101", subject: "Cấu trúc dữ liệu", teacher: "Nguyễn Văn A", students: 45, attendance: 58, status: "Cần chú ý", type: "error" as const },
    { id: "ENG202", subject: "Tiếng Anh Chuyên Ngành", teacher: "Trần Thị B", students: 30, attendance: 98, status: "Tốt", type: "success" as const },
    { id: "MATH301", subject: "Đại số tuyến tính", teacher: "Lê Văn C", students: 60, attendance: 85, status: "Bình thường", type: "neutral" as const },
    { id: "PHY101", subject: "Vật lý đại cương", teacher: "Phạm Thị D", students: 55, attendance: 92, status: "Tốt", type: "success" as const },
  ];

  const getBarColor = (type: string) => {
    switch (type) {
      case "error": return { bar: "bg-error", track: "bg-error-container" };
      case "success": return { bar: "bg-secondary", track: "bg-secondary-container" };
      default: return { bar: "bg-tertiary", track: "bg-tertiary-container/30" };
    }
  };

  const getBadgeStyle = (type: string) => {
    switch (type) {
      case "error": return "bg-error-container text-on-error-container";
      case "success": return "bg-secondary-container text-secondary";
      default: return "bg-tertiary-container/20 text-tertiary";
    }
  };

  const getKpiColor = (color: string) => {
    switch (color) {
      case "primary": return { bg: "bg-primary/10", text: "text-primary", accent: "bg-primary/5" };
      case "tertiary": return { bg: "bg-tertiary/10", text: "text-tertiary", accent: "bg-tertiary/5" };
      case "secondary": return { bg: "bg-secondary/10", text: "text-secondary", accent: "bg-secondary/5" };
      default: return { bg: "bg-primary/10", text: "text-primary", accent: "bg-primary/5" };
    }
  };

  return (
    <main className="flex-1 pt-24 px-xl pb-xl bg-background overflow-y-auto">
      {/* Page Header & Quick Actions */}
      <div className="flex justify-between items-start mb-lg">
        <div>
          <h1 className="font-h1 text-h1 text-on-surface">Bảng điều khiển</h1>
          <p className="text-body-md text-on-surface-variant mt-1">Chào mừng quay lại, Admin. Đây là tổng quan hệ thống hôm nay.</p>
        </div>
        <div className="flex gap-3">
          <Link href="/notifications" className="bg-[#6C63FF] text-white font-button text-button px-4 py-2 rounded-lg hover:bg-[#5a52d9] transition-colors duration-200 flex items-center gap-2 shadow-[0px_4px_12px_rgba(108,99,255,0.25)]">
            <span className="material-symbols-outlined text-[20px]">campaign</span>
            Gửi thông báo hệ thống
          </Link>
          <button className="bg-surface-container-highest text-on-surface font-button text-button px-4 py-2 rounded-lg hover:bg-surface-dim transition-colors duration-200 flex items-center gap-2">
            <span className="material-symbols-outlined text-[20px]">download</span>
            Xuất báo cáo
          </button>
        </div>
      </div>

      {/* KPI Bento Grid */}
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-container_gutter mb-container_gutter">
        {kpiData.map((kpi, idx) => {
          const colors = getKpiColor(kpi.color);
          return (
            <div
              key={idx}
              className="bg-surface-container-lowest rounded-xl p-lg shadow-[var(--shadow-card)] border border-border-muted flex flex-col justify-between relative overflow-hidden group"
            >
              <div className={`absolute top-0 right-0 w-24 h-24 ${colors.accent} rounded-bl-full -mr-4 -mt-4 group-hover:scale-110 transition-transform`}></div>
              <div className="flex justify-between items-start mb-md relative z-10">
                <div>
                  <p className="text-body-sm text-on-surface-variant mb-1">{kpi.label}</p>
                  <h3 className="text-h1 text-on-surface">{kpi.value}</h3>
                </div>
                <div className={`w-10 h-10 rounded-lg ${colors.bg} flex items-center justify-center ${colors.text}`}>
                  <span className="material-symbols-outlined">{kpi.icon}</span>
                </div>
              </div>
              <div className="flex items-center gap-1 relative z-10">
                {kpi.up ? (
                  <>
                    <span className="material-symbols-outlined text-[16px] text-secondary-fixed-dim">trending_up</span>
                    <span className="text-body-sm text-secondary-fixed-dim">
                      <strong className="font-semibold">{kpi.trend}</strong> {kpi.trendText}
                    </span>
                  </>
                ) : (
                  <>
                    <span className="material-symbols-outlined text-[16px] text-on-surface-variant">trending_flat</span>
                    <span className="text-body-sm text-on-surface-variant">{kpi.trendText}</span>
                  </>
                )}
              </div>
            </div>
          );
        })}

        {/* KPI 4 - Featured purple card */}
        <div className="bg-primary rounded-xl p-lg shadow-[var(--shadow-primary)] flex flex-col justify-between relative overflow-hidden text-white group">
          <div className="absolute top-0 right-0 w-32 h-32 bg-white/10 rounded-bl-full -mr-8 -mt-8 group-hover:scale-110 transition-transform"></div>
          <div className="flex justify-between items-start mb-md relative z-10">
            <div>
              <p className="text-body-sm text-white/80 mb-1">Tỷ lệ chuyên cần</p>
              <h3 className="text-h1">94.2%</h3>
            </div>
            <div className="w-10 h-10 rounded-lg bg-white/20 flex items-center justify-center">
              <span className="material-symbols-outlined">check_circle</span>
            </div>
          </div>
          <div className="relative z-10">
            <div className="w-full bg-white/20 rounded-full h-1.5 mt-2">
              <div className="bg-white h-1.5 rounded-full" style={{ width: "94.2%" }}></div>
            </div>
          </div>
        </div>
      </div>

      {/* Middle Section: Chart & Alerts */}
      <div className="grid grid-cols-1 lg:grid-cols-3 gap-container_gutter mb-container_gutter">
        {/* Bar Chart */}
        <div className="lg:col-span-2 bg-surface-container-lowest rounded-xl p-lg shadow-[var(--shadow-card)] border border-border-muted">
          <div className="flex justify-between items-center mb-lg">
            <h3 className="text-h3 text-on-surface">Tỷ lệ chuyên cần theo khoa (7 ngày qua)</h3>
            <button className="p-2 text-on-surface-variant hover:bg-surface-container-highest rounded-lg transition-colors">
              <span className="material-symbols-outlined">more_vert</span>
            </button>
          </div>
          <div className="h-64 flex items-end justify-between gap-2 pt-8">
            {chartData.map((item, idx) => (
              <div key={idx} className="flex flex-col items-center flex-1 gap-2 group">
                <div className="w-full bg-surface-container-high rounded-t-sm relative h-full flex items-end">
                  <div
                    className={`w-full rounded-t-sm transition-colors ${
                      item.highlighted
                        ? "bg-primary"
                        : "bg-primary/40 group-hover:bg-primary"
                    }`}
                    style={{ height: `${item.value}%` }}
                  ></div>
                  <span
                    className={`absolute -top-6 left-1/2 -translate-x-1/2 text-label ${
                      item.highlighted
                        ? "text-primary font-bold"
                        : "text-on-surface-variant opacity-0 group-hover:opacity-100 transition-opacity"
                    }`}
                  >
                    {item.value}%
                  </span>
                </div>
                <span
                  className={`text-label text-on-surface-variant truncate w-full text-center ${
                    item.highlighted ? "font-bold" : ""
                  }`}
                >
                  {item.label}
                </span>
              </div>
            ))}
          </div>
        </div>

        {/* System Alerts */}
        <div className="bg-surface-container-lowest rounded-xl p-lg shadow-[var(--shadow-card)] border border-border-muted flex flex-col">
          <h3 className="text-h3 text-on-surface mb-lg">Cảnh báo hệ thống</h3>
          <div className="flex flex-col gap-md flex-1">
            <div className="flex gap-md items-start p-md bg-error-container/30 rounded-lg border border-error-container">
              <span className="material-symbols-outlined text-error mt-0.5">warning</span>
              <div>
                <h4 className="text-button text-error">Lớp CS101 điểm danh thấp</h4>
                <p className="text-body-sm text-on-surface-variant mt-1">
                  Tỷ lệ chuyên cần dưới 60% trong 3 buổi liên tiếp.
                </p>
              </div>
            </div>
            <div className="flex gap-md items-start p-md bg-secondary-container/30 rounded-lg border border-secondary-container">
              <span className="material-symbols-outlined text-secondary mt-0.5">info</span>
              <div>
                <h4 className="text-button text-secondary">Bảo trì hệ thống</h4>
                <p className="text-body-sm text-on-surface-variant mt-1">
                  Lên lịch bảo trì server vào 02:00 AM Chủ Nhật tuần này.
                </p>
              </div>
            </div>
          </div>
          <Link href="/alerts" className="w-full mt-lg py-2 px-md bg-primary/10 text-primary hover:bg-primary/20 text-button rounded-lg transition-colors flex justify-center items-center">
            Xem tất cả cảnh báo
          </Link>
        </div>
      </div>

      {/* Table Section */}
      <div className="bg-surface-container-lowest rounded-xl shadow-[var(--shadow-card)] border border-border-muted overflow-hidden">
        <div className="p-lg border-b border-border-muted flex justify-between items-center">
          <h3 className="text-h3 text-on-surface">Lớp gần đây</h3>
          <div className="flex gap-2">
            <div className="relative">
              <span className="material-symbols-outlined absolute left-3 top-1/2 -translate-y-1/2 text-outline text-[20px]">search</span>
              <input
                className="pl-10 pr-4 py-2 border border-outline-variant rounded-lg text-body-sm focus:border-primary focus:ring-1 focus:ring-primary outline-none transition-all w-64 bg-surface-container-lowest"
                placeholder="Tìm kiếm lớp..."
                type="text"
              />
            </div>
            <button className="flex items-center gap-1 px-3 py-2 border border-outline-variant rounded-lg text-button text-on-surface-variant hover:bg-surface-container-highest transition-colors bg-surface-container-lowest">
              <span className="material-symbols-outlined text-[18px]">filter_list</span>
              Lọc
            </button>
          </div>
        </div>
        <div className="overflow-x-auto">
          <table className="w-full text-left border-collapse">
            <thead>
              <tr className="bg-surface-container-low border-b border-border-muted">
                <th className="py-md px-lg text-label text-on-surface-variant">Mã lớp</th>
                <th className="py-md px-lg text-label text-on-surface-variant">Môn học</th>
                <th className="py-md px-lg text-label text-on-surface-variant">Giảng viên</th>
                <th className="py-md px-lg text-label text-on-surface-variant">Sĩ số</th>
                <th className="py-md px-lg text-label text-on-surface-variant">Tỷ lệ chuyên cần</th>
                <th className="py-md px-lg text-label text-on-surface-variant text-right">Trạng thái</th>
              </tr>
            </thead>
            <tbody className="text-body-md text-on-surface">
              {tableData.map((row, idx) => {
                const barColor = getBarColor(row.type);
                return (
                  <tr
                    key={idx}
                    className={`border-b border-border-muted/50 hover:bg-surface-container-highest/30 transition-colors ${
                      idx % 2 === 1 ? "bg-surface-container-low/30" : ""
                    }`}
                  >
                    <td className="py-md px-lg text-button text-primary">{row.id}</td>
                    <td className="py-md px-lg font-semibold">{row.subject}</td>
                    <td className="py-md px-lg text-on-surface-variant">{row.teacher}</td>
                    <td className="py-md px-lg">{row.students}</td>
                    <td className="py-md px-lg">
                      <div className="flex items-center gap-2">
                        <span className="w-12 text-right">{row.attendance}%</span>
                        <div className={`w-16 h-1.5 ${barColor.track} rounded-full overflow-hidden`}>
                          <div
                            className={`h-full ${barColor.bar}`}
                            style={{ width: `${row.attendance}%` }}
                          ></div>
                        </div>
                      </div>
                    </td>
                    <td className="py-md px-lg text-right">
                      <span className={`inline-flex items-center px-2 py-1 rounded-md text-label ${getBadgeStyle(row.type)}`}>
                        {row.status}
                      </span>
                    </td>
                  </tr>
                );
              })}
            </tbody>
          </table>
        </div>
      </div>
    </main>
  );
}
