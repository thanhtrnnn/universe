"use client";
import React, { useState } from "react";

const sessions = [
  { buoi: "Buổi 1", ngay: "05/09/2023", tong: 60, coMat: 51, diTre: 6, vang: 3 },
  { buoi: "Buổi 2", ngay: "12/09/2023", tong: 60, coMat: 54, diTre: 4, vang: 2 },
  { buoi: "Buổi 3", ngay: "19/09/2023", tong: 60, coMat: 48, diTre: 7, vang: 5 },
];

const barData = [
  { present: 85, late: 10, absent: 5 },
  { present: 90, late: 8, absent: 2 },
  { present: 80, late: 12, absent: 8 },
  { present: 90, late: 6, absent: 4 },
  { present: 75, late: 15, absent: 10 },
  { present: 90, late: 5, absent: 5 },
  { present: 90, late: 8, absent: 2 },
  { present: 70, late: 18, absent: 12 },
  { present: 90, late: 6, absent: 4 },
  { present: 90, late: 7, absent: 3 },
];

export default function StatisticsPage() {
  const [cls, setCls] = useState("CS101");
  const [from, setFrom] = useState("2023-09-01");
  const [to, setTo] = useState("2023-11-15");

  return (
    <main className="flex-1 overflow-y-auto p-xl bg-surface-container-low flex flex-col gap-container_gutter">
      {/* Header */}
      <div className="flex flex-col md:flex-row justify-between items-start md:items-center gap-4">
        <div>
          <h1 className="font-h1 text-h1 text-on-surface">Thống kê chuyên cần</h1>
          <p className="font-body-md text-on-surface-variant mt-1">Theo dõi tình hình tham gia lớp học của sinh viên</p>
        </div>
        <div className="flex items-center gap-md">
          <button className="flex items-center gap-2 px-md py-2 bg-surface-container-lowest border border-outline text-on-surface rounded font-button text-button hover:bg-surface-variant transition-colors shadow-sm">
            <span className="material-symbols-outlined" style={{ fontSize: 18 }}>description</span>Xuất PDF
          </button>
          <button className="flex items-center gap-2 px-md py-2 bg-primary text-on-primary rounded font-button text-button hover:opacity-90 transition-colors shadow-sm">
            <span className="material-symbols-outlined" style={{ fontSize: 18 }}>table_view</span>Xuất Excel
          </button>
        </div>
      </div>

      {/* Filters */}
      <div className="bg-surface-container-lowest p-lg rounded-lg shadow-sm border border-outline-variant/30 flex flex-wrap gap-lg items-end">
        <div className="flex flex-col gap-sm w-full md:w-64">
          <label className="font-label text-label text-on-surface-variant uppercase">Lớp học phần</label>
          <div className="relative">
            <select value={cls} onChange={e => setCls(e.target.value)}
              className="w-full appearance-none bg-surface border border-outline text-on-surface rounded px-md py-2 focus:border-primary focus:ring-1 focus:ring-primary outline-none font-body-md cursor-pointer pr-10">
              <option value="CS101">CS101 - Nhập môn Khoa học Máy tính</option>
              <option value="CS102">CS102 - Cấu trúc dữ liệu</option>
              <option value="SE201">SE201 - Công nghệ phần mềm</option>
            </select>
            <span className="material-symbols-outlined absolute right-3 top-1/2 -translate-y-1/2 pointer-events-none text-outline">arrow_drop_down</span>
          </div>
        </div>
        <div className="flex flex-col gap-sm w-full md:w-48">
          <label className="font-label text-label text-on-surface-variant uppercase">Từ ngày</label>
          <input type="date" value={from} onChange={e => setFrom(e.target.value)}
            className="w-full bg-surface border border-outline text-on-surface rounded px-md py-2 focus:border-primary focus:ring-1 focus:ring-primary outline-none font-body-md text-sm cursor-pointer" />
        </div>
        <div className="flex flex-col gap-sm w-full md:w-48">
          <label className="font-label text-label text-on-surface-variant uppercase">Đến ngày</label>
          <input type="date" value={to} onChange={e => setTo(e.target.value)}
            className="w-full bg-surface border border-outline text-on-surface rounded px-md py-2 focus:border-primary focus:ring-1 focus:ring-primary outline-none font-body-md text-sm cursor-pointer" />
        </div>
        <button className="px-md py-2 bg-surface-variant text-on-surface rounded font-button text-button hover:bg-surface-dim transition-colors h-[42px]">
          Lọc dữ liệu
        </button>
      </div>

      {/* KPI Cards */}
      <div className="grid grid-cols-1 md:grid-cols-4 gap-container_gutter">
        <div className="bg-surface-container-lowest p-lg rounded-lg shadow-sm border border-outline-variant/30 flex flex-col justify-between">
          <span className="font-label text-label text-on-surface-variant uppercase mb-2">Tổng số buổi</span>
          <div className="flex items-end justify-between">
            <span className="text-3xl font-bold text-on-surface">15</span>
            <span className="material-symbols-outlined text-outline">calendar_month</span>
          </div>
        </div>
        <div className="bg-surface-container-lowest p-lg rounded-lg shadow-sm border border-outline-variant/30 border-b-4 border-secondary flex flex-col justify-between">
          <span className="font-label text-label text-on-surface-variant uppercase mb-2">Tỉ lệ Có mặt TB</span>
          <div className="flex items-end justify-between">
            <span className="text-3xl font-bold text-on-surface">85.4%</span>
            <div className="flex items-center text-secondary text-sm font-semibold">
              <span className="material-symbols-outlined" style={{ fontSize: 16 }}>trending_up</span>
              <span>+2.1%</span>
            </div>
          </div>
        </div>
        <div className="bg-surface-container-lowest p-lg rounded-lg shadow-sm border border-outline-variant/30 border-b-4 border-tertiary flex flex-col justify-between">
          <span className="font-label text-label text-on-surface-variant uppercase mb-2">Tỉ lệ Đi trễ TB</span>
          <div className="flex items-end justify-between">
            <span className="text-3xl font-bold text-on-surface">9.2%</span>
            <div className="flex items-center text-on-surface-variant text-sm font-semibold">
              <span className="material-symbols-outlined" style={{ fontSize: 16 }}>trending_flat</span>
              <span>→0.0%</span>
            </div>
          </div>
        </div>
        <div className="bg-surface-container-lowest p-lg rounded-lg shadow-sm border border-outline-variant/30 border-b-4 border-error flex flex-col justify-between">
          <span className="font-label text-label text-on-surface-variant uppercase mb-2">Tỉ lệ Vắng TB</span>
          <div className="flex items-end justify-between">
            <span className="text-3xl font-bold text-on-surface">5.4%</span>
            <div className="flex items-center text-error text-sm font-semibold">
              <span className="material-symbols-outlined" style={{ fontSize: 16 }}>trending_down</span>
              <span>-1.5%</span>
            </div>
          </div>
        </div>
      </div>

      {/* Chart */}
      <div className="bg-surface-container-lowest p-lg rounded-xl shadow-sm border border-outline-variant/30">
        <div className="flex justify-between items-center mb-6">
          <h3 className="font-h3 text-h3 text-on-surface">Biểu đồ chuyên cần theo buổi học - CS101</h3>
          <div className="flex items-center gap-4 text-sm text-on-surface-variant">
            <div className="flex items-center gap-1.5"><div className="w-3 h-3 rounded-sm bg-secondary" /><span>Có mặt</span></div>
            <div className="flex items-center gap-1.5"><div className="w-3 h-3 rounded-sm bg-tertiary-fixed" /><span>Đi trễ</span></div>
            <div className="flex items-center gap-1.5"><div className="w-3 h-3 rounded-sm bg-error-container" /><span>Vắng mặt</span></div>
          </div>
        </div>
        <div className="relative h-[300px] w-full flex items-end pt-4 pb-8 pl-8 pr-2">
          <div className="absolute inset-0 flex flex-col justify-between pb-8 pl-8">
            {["100%","75%","50%","25%","0%"].map((l, i) => (
              <div key={i} className={`w-full ${i === 4 ? "border-t border-solid border-outline-variant" : "border-t border-dashed border-outline-variant/30"} flex items-center h-0 relative`}>
                <span className="absolute -left-8 text-xs text-on-surface-variant w-6 text-right">{l}</span>
              </div>
            ))}
          </div>
          <div className="relative z-10 w-full h-full flex justify-between items-end px-4 gap-2">
            {barData.map((b, i) => (
              <div key={i} className="flex flex-col justify-end w-full max-w-[40px] h-full group relative">
                <div className="absolute -top-12 left-1/2 -translate-x-1/2 bg-inverse-surface text-inverse-on-surface text-xs p-2 rounded opacity-0 group-hover:opacity-100 transition-opacity z-20 whitespace-nowrap pointer-events-none">
                  Buổi {i + 1}<br />Có mặt: {b.present}%, Trễ: {b.late}%, Vắng: {b.absent}%
                </div>
                <div className="w-full bg-error-container rounded-t-sm" style={{ height: `${b.absent}%` }} />
                <div className="w-full bg-tertiary-fixed" style={{ height: `${b.late}%` }} />
                <div className="w-full bg-secondary rounded-b-sm" style={{ height: `${b.present}%` }} />
                <span className={`absolute -bottom-6 left-1/2 -translate-x-1/2 text-xs whitespace-nowrap ${i === 7 ? "text-error font-semibold" : "text-on-surface-variant"}`}>
                  Buổi {i + 1}
                </span>
              </div>
            ))}
          </div>
        </div>
      </div>

      {/* Detail Table */}
      <div className="bg-surface-container-lowest rounded-xl shadow-sm border border-outline-variant/30 overflow-hidden flex flex-col">
        <div className="p-lg border-b border-outline-variant/30">
          <h3 className="font-h3 text-h3 text-on-surface">Chi tiết điểm danh</h3>
        </div>
        <div className="overflow-x-auto">
          <table className="w-full text-left border-collapse">
            <thead>
              <tr className="bg-surface-container-low border-b border-outline-variant/30">
                {["Buổi học","Ngày","Tổng SV","Có mặt","Đi trễ","Vắng"].map(h => (
                  <th key={h} className="py-3 px-lg font-label text-label text-on-surface-variant uppercase whitespace-nowrap">{h}</th>
                ))}
              </tr>
            </thead>
            <tbody className="font-body-md text-on-surface">
              {sessions.map((s, i) => (
                <tr key={i} className={`border-b border-outline-variant/20 hover:bg-surface-bright transition-colors ${i % 2 === 1 ? "bg-surface-container-lowest" : ""}`}>
                  <td className="py-3 px-lg font-semibold">{s.buoi}</td>
                  <td className="py-3 px-lg text-on-surface-variant">{s.ngay}</td>
                  <td className="py-3 px-lg">{s.tong}</td>
                  <td className="py-3 px-lg text-secondary font-semibold">{s.coMat} ({Math.round(s.coMat/s.tong*100)}%)</td>
                  <td className="py-3 px-lg text-tertiary">{s.diTre} ({Math.round(s.diTre/s.tong*100)}%)</td>
                  <td className="py-3 px-lg text-error">{s.vang} ({Math.round(s.vang/s.tong*100)}%)</td>
                </tr>
              ))}
              <tr className="hover:bg-surface-bright transition-colors bg-surface-container-lowest">
                <td className="py-3 px-lg font-semibold text-center text-on-surface-variant" colSpan={6}>... Tải thêm dữ liệu ...</td>
              </tr>
            </tbody>
          </table>
        </div>
      </div>
    </main>
  );
}
