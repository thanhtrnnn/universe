"use client";

import React, { useState } from "react";
import { useParams } from "next/navigation";

export default function ClassAttendancePage() {
  const params = useParams();
  const classId = params.id as string;
  const [filter, setFilter] = useState("all");

  const students = [
    { 
      id: "20201234", 
      name: "Nguyễn Văn An", 
      status: [true, true, true, false], 
      current: true, 
      rate: "80%", 
      avatar: "https://lh3.googleusercontent.com/aida-public/AB6AXuC4Jyu6ZSVhVjgA27G_RJAvkc0EzeLmtDDrJ396s2_0EnSv9D051_D49omvfFZtCg723BZW0dFHt1iAK5fpSvDidC6hO8HbQxo3yWlG_ATLivocMAjItwNFKD_iP8Z9DhBCnpf9zjXbXzfQFSs7KKicIG1Ag1GHc-oISa75SJcPlQtB0UqHgCdMYrLyH53-9_ZiKbkIYVGlu8mrU-HN54c8xOHEdAEC0PL7qxtuCnm59ELCLQXrRgRYFfI5RQ_lfYB_sbQO1Kw9DXs" 
    },
    { 
      id: "20205678", 
      name: "Trần Thị Bích", 
      status: [true, true, true, true], 
      current: true, 
      rate: "100%", 
      avatar: "https://lh3.googleusercontent.com/aida-public/AB6AXuCH-8_T1xWrhq_jCw0cAbVdCQAa8f6Ed211MU0FKegL2TXF-bcsuaqAaI9NGCsSB9ykK0NckQy0xN4G_wHvPw_4I6M7pW-b_rq9EdlrAq1I5nFiuseDr5M3k4eG40RmZn62SrvxeWCohxHqzvf0cErn6h_DamBwsNpisjL7SDScuIMCfqgkkGvrSEHsmoJXJmQtCidwtZ1QrZQhbS6fkVS8i3wlhMErbSFUkfaskf7u-56wu2QlRhUy5XUdyIVMBZR1qNXpvK6_N5A" 
    },
    { 
      id: "20209012", 
      name: "Lê Công Quốc", 
      status: [true, null, false, true], 
      current: false, 
      rate: "40%", 
      initials: "CQ" 
    },
    { 
      id: "20203456", 
      name: "Phạm Hữu Dũng", 
      status: [true, true, true, true], 
      current: true, 
      rate: "100%", 
      avatar: "https://lh3.googleusercontent.com/aida-public/AB6AXuAbP72lG0tvbeR8oPT-sGQ_c3kZN-gMLO3GPjw6zZzqIsSR5BgU01hWYZKkhZ4TgRzeHNIfvmNo4J0D10G4TaTdMeDfd6YLpxaYdpT7gRG_5KFq9iv13DlAbep1endHmuYWNVfwLu9vKHyNybrA8fkFMIJPJyNDWstTOp0Z1kmlfrXUO8JPc7v0iZ3TdwSKZ7GRTmaE-sFyMN9sSufq2MwLorvJZjyLM_PMHzXK4IgC2jDGLeExiNRe1ACFgXt2aVWVDOIRmyzZMpA" 
    },
  ];

  const sessions = [
    { label: "B1", val: 95, color: "bg-primary-fixed-dim" },
    { label: "B2", val: 98, color: "bg-primary-fixed-dim" },
    { label: "B3", val: 85, color: "bg-primary-fixed-dim" },
    { label: "B4", val: 92, color: "bg-primary-fixed-dim" },
    { label: "B5", val: 89, color: "bg-primary", active: true },
    { label: "B6", val: 5, color: "bg-surface-container", inactive: true },
    { label: "B7", val: 5, color: "bg-surface-container", inactive: true },
  ];

  return (
    <main className="animate-in fade-in slide-in-from-bottom-4 duration-700 flex flex-col gap-container_gutter max-w-full overflow-x-hidden">
      {/* Page Header & Actions */}
      <div className="flex flex-col md:flex-row justify-between items-start md:items-center gap-6">
        <div>
          <h2 className="font-h1 text-h1 text-on-background">Cấu trúc dữ liệu & Giải thuật</h2>
          <div className="flex flex-wrap items-center gap-3 mt-2 text-on-surface-variant font-body-sm">
            <span className="bg-surface-variant px-3 py-1 rounded-lg font-black text-[10px] uppercase tracking-widest">Mã HP: IT3011</span>
            <span className="text-outline-variant opacity-30">•</span>
            <span className="font-bold text-[11px] uppercase tracking-tighter">Nhóm: 02</span>
            <span className="text-outline-variant opacity-30">•</span>
            <span className="font-bold text-[11px] uppercase tracking-tighter">Học kỳ: 2023.2</span>
          </div>
        </div>
        <div className="flex flex-wrap items-center gap-3">
          <button className="flex items-center gap-2 font-black text-[10px] uppercase tracking-widest px-5 py-3 border border-outline-variant text-on-surface hover:bg-surface-container-low rounded-xl transition-all active:scale-95">
            <span className="material-symbols-outlined text-[18px]">edit</span>
            Chỉnh sửa
          </button>
          <div className="relative group">
            <button className="flex items-center gap-2 font-black text-[10px] uppercase tracking-widest px-5 py-3 bg-primary-fixed text-on-primary-fixed hover:bg-primary-fixed-dim rounded-xl transition-all active:scale-95">
              <span className="material-symbols-outlined text-[18px]">download</span>
              Xuất báo cáo
              <span className="material-symbols-outlined text-[18px]">expand_more</span>
            </button>
            <div className="absolute right-0 top-full mt-2 w-48 bg-surface-container-lowest border border-outline-variant/30 rounded-xl shadow-xl opacity-0 translate-y-2 pointer-events-none group-hover:opacity-100 group-hover:translate-y-0 group-hover:pointer-events-auto transition-all z-50 flex flex-col py-2">
              <button className="px-4 py-3 text-[11px] font-black uppercase tracking-widest hover:bg-surface-container-low flex items-center gap-3 transition-colors text-error">
                <span className="material-symbols-outlined text-[18px]">picture_as_pdf</span>
                Tải xuống PDF
              </button>
              <button className="px-4 py-3 text-[11px] font-black uppercase tracking-widest hover:bg-surface-container-low flex items-center gap-3 transition-colors text-secondary">
                <span className="material-symbols-outlined text-[18px]">table_view</span>
                Xuất Excel (.xlsx)
              </button>
            </div>
          </div>
          <button className="flex items-center gap-2 font-black text-[10px] uppercase tracking-widest px-6 py-3 bg-primary text-on-primary hover:bg-primary/90 rounded-xl transition-all shadow-lg shadow-primary/20 active:scale-95">
            <span className="material-symbols-outlined text-[18px] fill">save</span>
            Lưu điểm danh
          </button>
        </div>
      </div>

      {/* Bento Grid Layout */}
      <div className="grid grid-cols-12 gap-container_gutter">
        {/* Stats Card */}
        <div className="col-span-12 lg:col-span-4 bg-surface-container-lowest rounded-2xl shadow-[0px_4px_20px_rgba(26,26,26,0.05)] border border-outline-variant/30 p-lg flex flex-col justify-between group hover:border-primary/20 transition-all">
          <div>
            <h3 className="font-h3 text-xl font-black text-on-surface mb-8 flex items-center gap-3">
              <span className="material-symbols-outlined text-primary fill">analytics</span>
              Tổng quan sỉ số
            </h3>
            <div className="flex items-center gap-5 mb-8">
              <div className="w-14 h-14 rounded-2xl bg-primary-fixed flex items-center justify-center text-primary shadow-inner group-hover:scale-110 transition-transform">
                <span className="material-symbols-outlined text-3xl fill">groups</span>
              </div>
              <div>
                <p className="font-black text-[10px] text-on-surface-variant uppercase tracking-widest opacity-60">Tổng sinh viên</p>
                <p className="text-4xl font-black text-on-surface leading-none mt-1">65</p>
              </div>
            </div>
            <div className="space-y-3">
              <div className="flex items-center justify-between">
                <span className="text-[11px] font-black uppercase tracking-tighter text-on-surface-variant">Tỉ lệ đi học trung bình</span>
                <span className="text-sm font-black text-primary">92%</span>
              </div>
              <div className="w-full h-3 bg-surface-container-highest rounded-full overflow-hidden p-0.5">
                <div className="h-full bg-primary rounded-full transition-all duration-1000 shadow-sm" style={{ width: "92%" }}></div>
              </div>
            </div>
          </div>
          <div className="mt-8 pt-6 border-t border-outline-variant/30 flex items-center justify-between text-[11px] font-black uppercase tracking-widest">
            <span className="text-on-surface-variant opacity-60">Buổi học hiện tại:</span>
            <span className="text-primary">Buổi 5 (12/10/2023)</span>
          </div>
        </div>

        {/* Chart Card */}
        <div className="col-span-12 lg:col-span-8 bg-surface-container-lowest rounded-2xl shadow-[0px_4px_20px_rgba(26,26,26,0.05)] border border-outline-variant/30 p-lg group hover:border-primary/20 transition-all">
          <div className="flex justify-between items-center mb-8">
            <h3 className="font-h3 text-xl font-black text-on-surface flex items-center gap-3">
              <span className="material-symbols-outlined text-secondary fill">bar_chart</span>
              Biểu đồ chuyên cần
            </h3>
            <button className="p-2 hover:bg-surface-container-low rounded-xl transition-colors text-on-surface-variant">
              <span className="material-symbols-outlined">more_horiz</span>
            </button>
          </div>
          <div className="h-[200px] flex items-end justify-between gap-4 mt-6 px-4">
            {sessions.map((s, i) => (
              <div key={i} className="flex flex-col items-center gap-4 flex-1 group/bar">
                <div 
                  className={`w-full max-w-[56px] rounded-t-xl relative transition-all duration-700 cursor-pointer ${
                    s.active 
                      ? "bg-primary shadow-xl shadow-primary/20 scale-105" 
                      : s.inactive 
                        ? "bg-surface-container border border-dashed border-outline-variant/50" 
                        : s.val < 90 ? "bg-error/40 hover:bg-error transition-colors" : "bg-primary-fixed-dim hover:bg-primary transition-colors"
                  }`} 
                  style={{ height: `${s.val}%` }}
                >
                  {!s.inactive && (
                    <span className="absolute -top-10 left-1/2 -translate-x-1/2 text-[10px] font-black text-on-primary bg-primary px-2.5 py-1 rounded-lg shadow-lg opacity-0 group-hover/bar:opacity-100 transition-all translate-y-2 group-hover/bar:translate-y-0 pointer-events-none whitespace-nowrap">
                      {s.val} SV
                    </span>
                  )}
                </div>
                <span className={`text-[10px] font-black uppercase tracking-widest ${s.active ? "text-primary" : "text-on-surface-variant opacity-60"}`}>
                  {s.label}
                </span>
              </div>
            ))}
          </div>
        </div>
      </div>

      {/* Detailed Attendance Table */}
      <div className="bg-surface-container-lowest rounded-2xl shadow-[0px_4px_20px_rgba(26,26,26,0.05)] border border-outline-variant/30 overflow-hidden group/table">
        <div className="p-lg border-b border-outline-variant/30 flex flex-col sm:flex-row justify-between items-start sm:items-center gap-4 bg-surface-container-lowest">
          <h3 className="font-h3 text-xl font-black text-on-surface flex items-center gap-3">
            <span className="material-symbols-outlined text-tertiary fill">list_alt</span>
            Danh sách sinh viên
          </h3>
          <div className="flex items-center bg-surface-container-low p-1.5 rounded-xl border border-outline-variant/10 shadow-inner">
            <button 
              onClick={() => setFilter("all")}
              className={`px-5 py-2 text-[10px] font-black uppercase tracking-widest rounded-lg transition-all ${filter === 'all' ? 'bg-surface-container-lowest shadow-md text-primary' : 'text-on-surface-variant hover:text-on-surface'}`}
            >
              Tất cả
            </button>
            <button 
              onClick={() => setFilter("absent")}
              className={`px-5 py-2 text-[10px] font-black uppercase tracking-widest rounded-lg transition-all ${filter === 'absent' ? 'bg-surface-container-lowest shadow-md text-error' : 'text-on-surface-variant hover:text-on-surface'}`}
            >
              Vắng mặt (7)
            </button>
          </div>
        </div>
        
        <div className="overflow-x-auto no-scrollbar">
          <table className="w-full text-left border-collapse min-w-[1000px]">
            <thead>
              <tr className="bg-surface-container-low/50 border-b border-outline-variant/30">
                <th className="py-5 px-6 font-black text-[10px] text-on-surface-variant uppercase tracking-widest text-center w-20">STT</th>
                <th className="py-5 px-6 font-black text-[10px] text-on-surface-variant uppercase tracking-widest min-w-[280px]">SINH VIÊN</th>
                {[1, 2, 3, 4].map(b => (
                  <th key={b} className="py-5 px-4 font-black text-[10px] text-on-surface-variant uppercase tracking-widest text-center">BUỔI {b}</th>
                ))}
                <th className="py-5 px-4 font-black text-[10px] text-primary uppercase tracking-widest text-center bg-primary-fixed-dim/30 border-x border-primary-fixed">BUỔI 5 (Hôm nay)</th>
                <th className="py-5 px-6 font-black text-[10px] text-on-surface-variant uppercase tracking-widest text-center">% ĐI HỌC</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-outline-variant/10 font-body-sm text-on-surface">
              {students.map((student, idx) => (
                <tr key={student.id} className="hover:bg-surface-bright transition-colors group/row">
                  <td className="py-5 px-6 text-center text-on-surface-variant font-bold text-xs opacity-60">{idx + 1}</td>
                  <td className="py-5 px-6">
                    <div className="flex items-center gap-4">
                      {student.avatar ? (
                        <div className="w-11 h-11 rounded-full border-2 border-outline-variant/20 overflow-hidden shadow-sm group-hover/row:scale-110 transition-transform">
                          <img src={student.avatar} alt={student.name} className="w-full h-full object-cover" />
                        </div>
                      ) : (
                        <div className="w-11 h-11 rounded-full bg-tertiary-fixed flex items-center justify-center text-tertiary font-black text-xs uppercase shadow-inner group-hover/row:scale-110 transition-transform">
                          {student.initials}
                        </div>
                      )}
                      <div>
                        <p className="font-black text-on-surface text-sm group-hover/row:text-primary transition-colors">{student.name}</p>
                        <p className="text-[10px] text-on-surface-variant font-bold mt-0.5 tracking-widest opacity-60">{student.id}</p>
                      </div>
                    </div>
                  </td>
                  {student.status.map((h, i) => (
                    <td key={i} className="py-5 px-4 text-center">
                      {h === true ? (
                        <span className="material-symbols-outlined text-secondary fill text-[22px] drop-shadow-sm">check_circle</span>
                      ) : h === false ? (
                        <span className="material-symbols-outlined text-error fill text-[22px] drop-shadow-sm">cancel</span>
                      ) : (
                        <span className="material-symbols-outlined text-outline-variant/40 text-[22px]">remove</span>
                      )}
                    </td>
                  ))}
                  <td className="py-5 px-4 text-center bg-primary-fixed-dim/10 border-x border-primary-fixed/30 group-hover/row:bg-primary-fixed-dim/20 transition-colors">
                    <div className="flex justify-center">
                      <label className="relative flex items-center cursor-pointer group/check">
                        <input 
                          type="checkbox" 
                          className="peer sr-only"
                          defaultChecked={student.current}
                        />
                        <div className="w-6 h-6 bg-surface-container-highest rounded-lg border-2 border-outline-variant peer-checked:bg-primary peer-checked:border-primary transition-all flex items-center justify-center shadow-inner">
                          <span className="material-symbols-outlined text-white text-[18px] font-black opacity-0 peer-checked:opacity-100 transition-opacity">check</span>
                        </div>
                      </label>
                    </div>
                  </td>
                  <td className="py-5 px-6 text-center">
                    <span className={`px-3 py-1.5 rounded-xl text-[10px] font-black uppercase tracking-tighter shadow-sm border ${
                      parseInt(student.rate) >= 80 
                        ? "bg-secondary/10 text-secondary border-secondary/20" 
                        : "bg-error/10 text-error border-error/20"
                    }`}>
                      {student.rate}
                    </span>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>

        <div className="p-lg border-t border-outline-variant/30 flex flex-col sm:flex-row items-center justify-between gap-6 bg-surface-container-lowest">
          <span className="text-[11px] font-black uppercase tracking-widest text-on-surface-variant opacity-60">Hiển thị 1-4 trên 65 sinh viên</span>
          <div className="flex items-center gap-2">
            <button className="w-10 h-10 rounded-xl text-on-surface-variant hover:bg-surface-container-low transition-all disabled:opacity-20 flex items-center justify-center border border-outline-variant/30" disabled>
              <span className="material-symbols-outlined">chevron_left</span>
            </button>
            <button className="w-10 h-10 rounded-xl bg-primary text-on-primary text-xs font-black shadow-lg shadow-primary/20 active:scale-95 transition-transform">1</button>
            <button className="w-10 h-10 rounded-xl hover:bg-surface-container-low text-on-surface text-xs font-bold transition-all border border-outline-variant/30">2</button>
            <button className="w-10 h-10 rounded-xl hover:bg-surface-container-low text-on-surface text-xs font-bold transition-all border border-outline-variant/30">3</button>
            <span className="text-on-surface-variant px-2 font-black text-xs tracking-[0.2em] opacity-40">...</span>
            <button className="w-10 h-10 rounded-xl text-on-surface-variant hover:bg-surface-container-low transition-all flex items-center justify-center border border-outline-variant/30">
              <span className="material-symbols-outlined">chevron_right</span>
            </button>
          </div>
        </div>
      </div>
    </main>
  );
}
