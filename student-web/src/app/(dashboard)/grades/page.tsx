"use client";

import React, { useState } from "react";

export default function GradesPage() {
  const semestersData = [
    {
      id: 1,
      name: "Học kỳ 1 - Năm học 2025 - 2026",
      gpa10: 8.2,
      gpa4: 3.4,
      totalCredits: 15,
      grades: [
        { id: 101, code: "BAS1203", name: "Giải tích 1", credits: 3, point10: "8.5", point4: "3.5", pointC: "B+", result: "Đạt" },
        { id: 102, code: "BAS1151", name: "Triết học Mác - Lênin", credits: 3, point10: "7.0", point4: "3.0", pointC: "B", result: "Đạt" },
        { id: 103, code: "INT1306", name: "Cơ sở lập trình", credits: 3, point10: "9.0", point4: "4.0", pointC: "A+", result: "Đạt" },
        { id: 104, code: "FLA1101", name: "Tiếng Anh 1", credits: 4, point10: "8.0", point4: "3.5", pointC: "B+", result: "Đạt" },
        { id: 105, code: "BAS1106", name: "Giáo dục thể chất 1", credits: 2, point10: "8.5", point4: "4.0", pointC: "A", result: "Đạt" },
      ]
    },
    {
      id: 2,
      name: "Học kỳ 2 - Năm học 2025 - 2026",
      gpa10: 7.9,
      gpa4: 3.12,
      totalCredits: 16,
      grades: [
        { id: 1, code: "BAS1152", name: "Chủ nghĩa xã hội khoa học", credits: 2, point10: "6.5", point4: "2.5", pointC: "C+", result: "Đạt" },
        { id: 2, code: "INT13187", name: "Thực tập cơ sở", credits: 4, point10: "8.0", point4: "3.5", pointC: "B+", result: "Đạt" },
        { id: 3, code: "INT1340", name: "Nhập môn CNPM", credits: 3, point10: "9.5", point4: "4.0", pointC: "A+", result: "Đạt" },
        { id: 4, code: "INT1341", name: "Nhập môn trí tuệ nhân tạo", credits: 3, point10: "7.5", point4: "3.0", pointC: "B", result: "Đạt" },
        { id: 5, code: "INT14167", name: "Hệ quản trị CSDL", credits: 3, point10: "8.0", point4: "3.5", pointC: "B+", result: "Đạt" },
        { id: 6, code: "INT1434", name: "Lập trình web", credits: 3, point10: "3.0", point4: "0.0", pointC: "F", result: "Học lại" },
      ]
    }
  ];

  // Calculate cumulative stats
  const totalCreditsAccumulated = semestersData.reduce((sum, sem) => sum + sem.totalCredits, 0);
  const totalPoints10 = semestersData.reduce((sum, sem) => sum + sem.gpa10 * sem.totalCredits, 0);
  const totalPoints4 = semestersData.reduce((sum, sem) => sum + sem.gpa4 * sem.totalCredits, 0);
  const cumulativeGpa10 = (totalPoints10 / totalCreditsAccumulated).toFixed(2);
  const cumulativeGpa4 = (totalPoints4 / totalCreditsAccumulated).toFixed(2);

  return (
    <main className="flex-1 pt-24 px-xl pb-xl bg-background overflow-y-auto">
      <div className="mb-lg flex justify-between items-end">
        <div>
          <h1 className="font-h1 text-h1 text-on-background mb-2">Kết quả học tập</h1>
          <p className="text-on-surface-variant font-body-md">Theo dõi điểm số và quá trình học tập qua các học kỳ.</p>
        </div>
        <div className="flex gap-4">
          <div className="bg-primary/10 p-3 rounded-lg border border-primary/20 flex flex-col items-end">
            <span className="text-body-sm text-on-surface-variant">Tín chỉ tích lũy</span>
            <span className="text-h3 text-primary">{totalCreditsAccumulated}</span>
          </div>
          <div className="bg-tertiary/10 p-3 rounded-lg border border-tertiary/20 flex flex-col items-end">
            <span className="text-body-sm text-on-surface-variant">CPA (Hệ 4)</span>
            <span className="text-h3 text-tertiary">{cumulativeGpa4}</span>
          </div>
        </div>
      </div>

      <div className="flex flex-col gap-8">
        {semestersData.map((semester) => (
          <div key={semester.id} className="bg-surface-container-lowest rounded-xl shadow-card border border-outline-variant/30 overflow-hidden">
            <div className="p-4 flex flex-col sm:flex-row justify-between items-center bg-surface-container-low/50 border-b border-outline-variant/30">
              <h2 className="font-h3 text-h3 text-on-surface">{semester.name}</h2>
              <div className="flex gap-4 text-body-sm mt-2 sm:mt-0 bg-surface px-4 py-2 rounded-full border border-outline-variant/50">
                <span className="text-on-surface-variant">GPA (Hệ 10): <strong className="text-on-surface">{semester.gpa10}</strong></span>
                <span className="w-px h-4 bg-outline-variant"></span>
                <span className="text-on-surface-variant">GPA (Hệ 4): <strong className="text-on-surface">{semester.gpa4}</strong></span>
                <span className="w-px h-4 bg-outline-variant"></span>
                <span className="text-on-surface-variant">Số TC: <strong className="text-on-surface">{semester.totalCredits}</strong></span>
              </div>
            </div>

            <div className="overflow-x-auto">
              <table className="w-full text-left border-collapse min-w-[900px]">
                <thead className="bg-surface-container-lowest">
                  <tr>
                    <th className="p-4 font-label text-label text-on-surface-variant border-b border-outline-variant/30">Mã MH</th>
                    <th className="p-4 font-label text-label text-on-surface-variant border-b border-outline-variant/30 w-full">Tên môn học</th>
                    <th className="p-4 font-label text-label text-on-surface-variant border-b border-outline-variant/30 text-center">Số TC</th>
                    <th className="p-4 font-label text-label text-on-surface-variant border-b border-outline-variant/30 text-center">Điểm TK (10)</th>
                    <th className="p-4 font-label text-label text-on-surface-variant border-b border-outline-variant/30 text-center">Điểm TK (4)</th>
                    <th className="p-4 font-label text-label text-on-surface-variant border-b border-outline-variant/30 text-center">Điểm chữ</th>
                    <th className="p-4 font-label text-label text-on-surface-variant border-b border-outline-variant/30 text-center">Kết quả</th>
                  </tr>
                </thead>
                <tbody>
                  {semester.grades.map((g) => (
                    <tr key={g.id} className="hover:bg-surface-container-low border-b border-outline-variant/10 last:border-b-0 transition-colors">
                      <td className="p-4 font-body-md text-on-surface font-medium">{g.code}</td>
                      <td className="p-4 font-body-md text-on-surface">{g.name}</td>
                      <td className="p-4 font-body-md text-on-surface text-center">{g.credits}</td>
                      <td className="p-4 font-body-md text-on-surface text-center font-semibold">{g.point10}</td>
                      <td className="p-4 font-body-md text-on-surface text-center">{g.point4}</td>
                      <td className="p-4 font-body-md text-on-surface text-center">
                        <span className={`px-2 py-1 rounded text-[12px] font-bold ${['A+', 'A', 'B+', 'B'].includes(g.pointC) ? 'text-primary' : g.pointC === 'F' ? 'text-error bg-error-container/20' : 'text-on-surface-variant'}`}>
                          {g.pointC}
                        </span>
                      </td>
                      <td className="p-4 font-body-md text-center">
                        <span className={`px-2 py-1 rounded text-[11px] font-semibold ${g.result === 'Đạt' ? 'bg-tertiary-container/50 text-on-tertiary-container' : 'bg-error-container text-on-error-container'}`}>
                          {g.result}
                        </span>
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          </div>
        ))}
      </div>
    </main>
  );
}
