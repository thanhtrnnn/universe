"use client";

import React from "react";

export default function ExamSchedulePage() {
  const exams = [
    { id: 1, course: "Cơ sở dữ liệu", code: "INT1311", date: "20/06/2026", time: "07:30", room: "A2-205", format: "Trắc nghiệm máy", duration: "60 phút" },
    { id: 2, course: "Cấu trúc dữ liệu", code: "INT1313", date: "23/06/2026", time: "13:30", room: "A3-401", format: "Tự luận", duration: "90 phút" },
    { id: 3, course: "Lập trình Web", code: "INT1332", date: "26/06/2026", time: "09:30", room: "A2-301", format: "Bảo vệ bài tập lớn", duration: "120 phút" },
    { id: 4, course: "Hệ điều hành", code: "INT1315", date: "30/06/2026", time: "15:30", room: "B1-102", format: "Tự luận", duration: "90 phút" },
  ];

  return (
    <main className="flex-1 pt-24 px-xl pb-xl bg-background overflow-y-auto">
      <div className="mb-lg">
        <h1 className="font-h1 text-h1 text-on-background">Xem lịch thi</h1>
      </div>
      <div className="bg-surface-container-lowest rounded-xl shadow-card border border-outline-variant/30 overflow-hidden flex flex-col h-[calc(100vh-180px)]">
        <div className="p-4 border-b border-outline-variant/30 flex justify-between items-center bg-surface-container-low/50">
          <select className="px-4 py-2 bg-surface border border-outline-variant rounded-lg text-body-md focus:outline-none focus:border-primary font-medium">
            <option>Học kỳ 2 - 2025-2026</option>
            <option>Học kỳ 1 - 2025-2026</option>
          </select>
          <div className="flex gap-2">
            <button className="flex items-center gap-2 px-4 py-2 border border-outline-variant rounded-lg text-button text-on-surface hover:bg-surface-container-low transition-colors">
              <span className="material-symbols-outlined text-[18px]">print</span> In
            </button>
            <button className="flex items-center gap-2 px-4 py-2 border border-outline-variant rounded-lg text-button text-on-surface hover:bg-surface-container-low transition-colors">
              <span className="material-symbols-outlined text-[18px]">download</span> Xuất Excel
            </button>
          </div>
        </div>

        <div className="flex-1 overflow-auto">
          <table className="w-full text-left border-collapse min-w-[900px]">
            <thead className="bg-surface-container-low sticky top-0 z-10">
              <tr>
                <th className="p-4 font-label text-label text-on-surface-variant border-b border-outline-variant/30">STT</th>
                <th className="p-4 font-label text-label text-on-surface-variant border-b border-outline-variant/30">Mã LHP</th>
                <th className="p-4 font-label text-label text-on-surface-variant border-b border-outline-variant/30">Tên môn học</th>
                <th className="p-4 font-label text-label text-on-surface-variant border-b border-outline-variant/30 text-center">Ngày thi</th>
                <th className="p-4 font-label text-label text-on-surface-variant border-b border-outline-variant/30 text-center">Giờ thi</th>
                <th className="p-4 font-label text-label text-on-surface-variant border-b border-outline-variant/30 text-center">Phòng thi</th>
                <th className="p-4 font-label text-label text-on-surface-variant border-b border-outline-variant/30">Hình thức</th>
                <th className="p-4 font-label text-label text-on-surface-variant border-b border-outline-variant/30 text-center">Thời lượng</th>
              </tr>
            </thead>
            <tbody>
              {exams.map((e, index) => (
                <tr key={e.id} className="hover:bg-surface-container-lowest border-b border-outline-variant/30 transition-colors">
                  <td className="p-4 font-body-md text-on-surface">{index + 1}</td>
                  <td className="p-4 font-body-md text-on-surface font-semibold">{e.code}</td>
                  <td className="p-4 font-body-md text-on-surface">{e.course}</td>
                  <td className="p-4 font-body-md text-on-surface text-center font-medium text-primary">{e.date}</td>
                  <td className="p-4 font-body-md text-on-surface text-center">{e.time}</td>
                  <td className="p-4 font-body-md text-on-surface text-center font-semibold">{e.room}</td>
                  <td className="p-4 font-body-md text-on-surface">
                    <span className="px-2 py-1 bg-secondary-container text-on-secondary-container rounded-md text-[12px] font-medium">
                      {e.format}
                    </span>
                  </td>
                  <td className="p-4 font-body-md text-on-surface text-center">{e.duration}</td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      </div>
    </main>
  );
}
