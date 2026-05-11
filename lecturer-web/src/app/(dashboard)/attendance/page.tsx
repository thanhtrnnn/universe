"use client";
import React, { useState } from "react";

export default function AttendancePage() {
  const students = [
    { stt: 1, mssv: "20201234", name: "Nguyễn Văn An", sessions: ["present", "present", "present", "absent"], current: "present", percent: "80%", avatar: "https://lh3.googleusercontent.com/aida-public/AB6AXuC4Jyu6ZSVhVjgA27G_RJAvkc0EzeLmtDDrJ396s2_0EnSv9D051_D49omvfFZtCg723BZW0dFHt1iAK5fpSvDidC6hO8HbQxo3yWlG_ATLivocMAjItwNFKD_iP8Z9DhBCnpf9zjXbXzfQFSs7KKicIG1Ag1GHc-oISa75SJcPlQtB0UqHgCdMYrLyH53-9_ZiKbkIYVGlu8mrU-HN54c8xOHEdAEC0PL7qxtuCnm59ELCLQXrRgRYFfI5RQ_lfYB_sbQO1Kw9DXs" },
    { stt: 2, mssv: "20205678", name: "Trần Thị Bích", sessions: ["present", "present", "present", "present"], current: "present", percent: "100%", avatar: "https://lh3.googleusercontent.com/aida-public/AB6AXuCH-8_T1xWrhq_jCw0cAbVdCQAa8f6Ed211MU0FKegL2TXF-bcsuaqAaI9NGCsSB9ykK0NckQy0xN4G_wHvPw_4I6M7pW-b_rq9EdlrAq1I5nFiuseDr5M3k4eG40RmZn62SrvxeWCohxHqzvf0cErn6h_DamBwsNpisjL7SDScuIMCfqgkkGvrSEHsmoJXJmQtCidwtZ1QrZQhbS6fkVS8i3wlhMErbSFUkfaskf7u-56wu2QlRhUy5XUdyIVMBZR1qNXpvK6_N5A" },
    { stt: 3, mssv: "20209012", name: "Lê Công Quốc", sessions: ["present", "absent", "absent", "present"], current: "absent", percent: "40%", initial: "CQ" },
    { stt: 4, mssv: "20203456", name: "Phạm Hữu Dũng", sessions: ["present", "present", "present", "present"], current: "present", percent: "100%", avatar: "https://lh3.googleusercontent.com/aida-public/AB6AXuAbP72lG0tvbeR8oPT-sGQ_c3kZN-gMLO3GPjw6zZzqIsSR5BgU01hWYZKkhZ4TgRzeHNIfvmNo4J0D10G4TaTdMeDfd6YLpxaYdpT7gRG_5KFq9iv13DlAbep1endHmuYWNVfwLu9vKHyNybrA8fkFMIJPJyNDWstTOp0Z1kmlfrXUO8JPc7v0iZ3TdwSKZ7GRTmaE-sFyMN9sSufq2MwLorvJZjyLM_PMHzXK4IgC2jDGLeExiNRe1ACFgXt2aVWVDOIRmyzZMpA" },
  ];

  return (
    <main className="flex-1 overflow-y-auto p-xl bg-background">
      {/* Page Header & Actions */}
      <div className="flex flex-col md:flex-row justify-between items-start md:items-center mb-lg gap-4">
        <div>
          <h2 className="font-h1 text-h1 text-on-background">Cấu trúc dữ liệu & Giải thuật</h2>
          <div className="flex items-center gap-2 mt-2 text-on-surface-variant font-body-sm text-body-sm">
            <span className="bg-surface-variant px-2 py-1 rounded">Mã HP: IT3011</span>
            <span className="text-outline">•</span>
            <span>Nhóm: 02</span>
            <span className="text-outline">•</span>
            <span>Học kỳ: 2023.2</span>
          </div>
        </div>
        <div className="flex items-center gap-3">
          <button className="flex items-center gap-2 font-button text-button px-4 py-2 border border-outline-variant text-on-surface hover:bg-surface-container-low rounded-lg transition-colors">
            <span className="material-symbols-outlined text-[18px]">edit</span>
            Chỉnh sửa
          </button>
          <button className="flex items-center gap-2 font-button text-button px-4 py-2 bg-primary-fixed text-on-primary-fixed hover:bg-primary-fixed-dim rounded-lg transition-colors">
            <span className="material-symbols-outlined text-[18px]">download</span>
            Xuất báo cáo
          </button>
          <button className="flex items-center gap-2 font-button text-button px-4 py-2 bg-primary text-on-primary hover:bg-primary/90 rounded-lg transition-colors shadow-sm">
            <span className="material-symbols-outlined text-[18px]">save</span>
            Lưu điểm danh
          </button>
        </div>
      </div>

      {/* Bento Grid Layout */}
      <div className="grid grid-cols-12 gap-container_gutter mb-xl">
        {/* Stats Card */}
        <div className="col-span-12 md:col-span-4 bg-surface-container-lowest rounded-xl shadow-[0px_4px_20px_rgba(26,26,26,0.05)] p-lg flex flex-col justify-between border border-outline-variant/30">
          <div>
            <h3 className="font-h3 text-h3 text-on-surface mb-6">Tổng quan sỉ số</h3>
            <div className="flex items-center gap-3 mb-6">
              <div className="w-10 h-10 rounded-full bg-primary-fixed flex items-center justify-center text-primary">
                <span className="material-symbols-outlined">groups</span>
              </div>
              <div>
                <p className="font-label text-label text-on-surface-variant">TỔNG SINH VIÊN</p>
                <p className="font-h2 text-h2 text-on-surface">65</p>
              </div>
            </div>
            <div className="flex items-center justify-between mb-2">
              <span className="font-body-sm text-body-sm text-on-surface-variant">Tỉ lệ đi học trung bình</span>
              <span className="font-label text-label text-primary">92%</span>
            </div>
            <div className="w-full h-2 bg-surface-container-highest rounded-full overflow-hidden">
              <div className="h-full bg-primary rounded-full" style={{ width: "92%" }}></div>
            </div>
          </div>
          <div className="mt-6 pt-4 border-t border-surface-variant flex items-center justify-between font-body-sm text-body-sm">
            <span className="text-on-surface-variant">Buổi học hiện tại:</span>
            <span className="font-bold text-on-surface">Buổi 5 (12/10/2023)</span>
          </div>
        </div>

        {/* Chart Card */}
        <div className="col-span-12 md:col-span-8 bg-surface-container-lowest rounded-xl shadow-[0px_4px_20px_rgba(26,26,26,0.05)] p-lg border border-outline-variant/30">
          <div className="flex justify-between items-center mb-6">
            <h3 className="font-h3 text-h3 text-on-surface">Biểu đồ chuyên cần theo buổi</h3>
            <button className="text-on-surface-variant hover:text-primary">
              <span className="material-symbols-outlined text-[20px]">more_horiz</span>
            </button>
          </div>
          <div className="h-[180px] flex items-end justify-between gap-4 px-2">
            {[95, 98, 85, 92, 89, 5, 5].map((val, i) => (
              <div key={i} className="flex flex-col items-center gap-2 flex-1 group">
                <div 
                  className={`w-full max-w-[40px] rounded-t-sm transition-colors relative ${
                    i === 4 ? 'bg-primary' : val > 5 ? 'bg-primary-fixed-dim hover:bg-primary' : 'bg-surface-container'
                  }`}
                  style={{ height: `${val}%` }}
                >
                  {val > 5 && (
                    <span className={`absolute -top-6 left-1/2 -translate-x-1/2 text-xs font-bold text-primary ${i === 4 ? 'opacity-100' : 'opacity-0 group-hover:opacity-100'} transition-opacity`}>
                      {Math.floor(val * 0.65)}
                    </span>
                  )}
                </div>
                <span className={`font-label text-label text-[10px] ${i === 4 ? 'text-primary font-bold' : 'text-on-surface-variant'}`}>B{i + 1}</span>
              </div>
            ))}
          </div>
        </div>
      </div>

      {/* Student List */}
      <div className="bg-surface-container-lowest rounded-xl shadow-[0px_4px_20px_rgba(26,26,26,0.05)] border border-outline-variant/30 overflow-hidden">
        <div className="p-lg border-b border-surface-variant flex justify-between items-center">
          <h3 className="font-h3 text-h3 text-on-surface">Danh sách sinh viên</h3>
          <div className="flex items-center bg-surface-container-low rounded-lg p-1">
            <button className="px-3 py-1 font-body-sm text-body-sm rounded bg-surface-container-lowest shadow-sm text-on-surface font-semibold">Tất cả</button>
            <button className="px-3 py-1 font-body-sm text-body-sm rounded text-on-surface-variant hover:text-on-surface transition-colors">Vắng mặt (7)</button>
          </div>
        </div>
        <div className="overflow-x-auto">
          <table className="w-full text-left border-collapse">
            <thead>
              <tr className="bg-surface-container-low border-b border-surface-variant">
                <th className="py-3 px-6 font-label text-label text-on-surface-variant text-center w-16">STT</th>
                <th className="py-3 px-6 font-label text-label text-on-surface-variant min-w-[250px]">SINH VIÊN</th>
                {[1, 2, 3, 4].map(b => (
                  <th key={b} className="py-3 px-4 font-label text-label text-on-surface-variant text-center">BUỔI {b}</th>
                ))}
                <th className="py-3 px-4 font-label text-label text-primary bg-primary-fixed-dim/30 text-center border-x border-primary-fixed">BUỔI 5 (Hôm nay)</th>
                <th className="py-3 px-6 font-label text-label text-on-surface-variant text-center">% ĐI HỌC</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-surface-variant">
              {students.map((s) => (
                <tr key={s.stt} className="hover:bg-surface-container-low/30 transition-colors group">
                  <td className="py-3 px-6 text-center text-on-surface-variant text-body-sm">{s.stt}</td>
                  <td className="py-3 px-6">
                    <div className="flex items-center gap-3">
                      {s.avatar ? (
                        <img src={s.avatar} alt={s.name} className="w-8 h-8 rounded-full object-cover border border-outline-variant" />
                      ) : (
                        <div className="w-8 h-8 rounded-full bg-tertiary-fixed flex items-center justify-center text-tertiary font-bold text-xs">{s.initial}</div>
                      )}
                      <div>
                        <p className="font-semibold text-body-sm text-on-surface">{s.name}</p>
                        <p className="text-[11px] text-on-surface-variant">{s.mssv}</p>
                      </div>
                    </div>
                  </td>
                  {s.sessions.map((status, i) => (
                    <td key={i} className="py-3 px-4 text-center">
                      <span className={`material-symbols-outlined text-[20px] ${status === 'present' ? 'text-secondary' : 'text-error'}`} style={{ fontVariationSettings: "'FILL' 1" }}>
                        {status === 'present' ? 'check_circle' : 'cancel'}
                      </span>
                    </td>
                  ))}
                  <td className="py-3 px-4 text-center bg-primary-fixed-dim/10 border-x border-primary-fixed/30 group-hover:bg-primary-fixed-dim/20 transition-colors">
                    <input 
                      type="checkbox" 
                      defaultChecked={s.current === 'present'}
                      className="w-5 h-5 text-secondary border-outline-variant rounded focus:ring-secondary cursor-pointer"
                    />
                  </td>
                  <td className={`py-3 px-6 text-center font-semibold text-body-sm ${s.percent === '100%' ? 'text-secondary' : s.percent === '40%' ? 'text-error' : 'text-on-surface'}`}>
                    {s.percent}
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      </div>
    </main>
  );
}

