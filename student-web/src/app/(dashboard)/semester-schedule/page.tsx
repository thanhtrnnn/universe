"use client";

import React from "react";

export default function SemesterSchedulePage() {
  const scheduleData = [
    {
      courseCode: "BAS1152",
      courseName: "Chủ nghĩa xã hội khoa học",
      group: "02",
      credits: 2,
      className: "D23CQCE01-B",
      schedules: [
        { day: 4, startPeriod: 3, numPeriods: 1, room: "503-A1", lecturer: "P.T.Khánh", timeRange: "01/04/26 đến 01/04/26" },
        { day: 4, startPeriod: 3, numPeriods: 1, room: "503-A1", lecturer: "P.T.Khánh", timeRange: "15/04/26 đến 15/04/26" },
        { day: 4, startPeriod: 3, numPeriods: 1, room: "503-A1", lecturer: "P.T.Khánh", timeRange: "29/04/26 đến 29/04/26" },
        { day: 4, startPeriod: 3, numPeriods: 1, room: "503-A1", lecturer: "P.T.Khánh", timeRange: "13/05/26 đến 13/05/26" },
        { day: 4, startPeriod: 4, numPeriods: 2, room: "503-A1", lecturer: "P.T.Khánh", timeRange: "14/01/26 đến 21/01/26" },
        { day: 4, startPeriod: 4, numPeriods: 2, room: "503-A1", lecturer: "P.T.Khánh", timeRange: "04/02/26 đến 04/02/26" },
        { day: 4, startPeriod: 4, numPeriods: 2, room: "503-A1", lecturer: "P.T.Khánh", timeRange: "04/03/26 đến 04/03/26" },
        { day: 4, startPeriod: 4, numPeriods: 2, room: "503-A1", lecturer: "P.T.Khánh", timeRange: "18/03/26 đến 18/03/26" },
        { day: 4, startPeriod: 18, numPeriods: 1, room: "LMS", lecturer: "P.T.Khánh", timeRange: "01/04/26 đến 01/04/26" },
        { day: 4, startPeriod: 18, numPeriods: 1, room: "LMS", lecturer: "P.T.Khánh", timeRange: "15/04/26 đến 15/04/26" },
        { day: 5, startPeriod: 18, numPeriods: 1, room: "LMS", lecturer: "P.T.Khánh", timeRange: "29/04/26 đến 29/04/26" },
        { day: 5, startPeriod: 18, numPeriods: 1, room: "LMS", lecturer: "P.T.Khánh", timeRange: "13/05/26 đến 13/05/26" },
        { day: 5, startPeriod: 18, numPeriods: 1, room: "LMS", lecturer: "P.T.Khánh", timeRange: "27/05/26 đến 27/05/26" },
      ]
    },
    {
      courseCode: "INT1340_CLC",
      courseName: "Nhập môn CNPM",
      group: "01",
      credits: 3,
      className: "D23CQCE01-B",
      schedules: [
        { day: 2, startPeriod: 1, numPeriods: 2, room: "503-A1", lecturer: "Đ.T.Liên", timeRange: "03/02/26 đến 17/02/26" },
        { day: 2, startPeriod: 1, numPeriods: 2, room: "503-A1", lecturer: "Đ.T.Liên", timeRange: "03/03/26 đến 17/03/26" },
        { day: 2, startPeriod: 1, numPeriods: 2, room: "503-A1", lecturer: "Đ.T.Liên", timeRange: "31/03/26 đến 14/04/26" },
        { day: 2, startPeriod: 3, numPeriods: 1, room: "503-A1", lecturer: "Đ.T.Liên", timeRange: "10/02/26 đến 10/02/26" },
        { day: 2, startPeriod: 3, numPeriods: 1, room: "503-A1", lecturer: "Đ.T.Liên", timeRange: "10/03/26 đến 10/03/26" },
      ]
    }
  ];

  return (
    <main className="flex-1 pt-24 px-xl pb-xl bg-background overflow-y-auto">
      <div className="mb-lg">
        <h1 className="font-h1 text-h1 text-on-background">Thời khóa biểu dạng học kỳ</h1>
        <div className="mt-4 p-4 bg-surface-container-lowest rounded-xl shadow-sm border border-outline-variant/30 flex flex-col md:flex-row gap-4 items-center justify-between">
          <div className="flex flex-wrap gap-4 items-center w-full md:w-auto">
            <select className="px-4 py-2 bg-surface border border-outline-variant rounded-lg text-body-md focus:outline-none focus:border-primary font-medium min-w-[250px]">
              <option>Học kỳ 2 - Năm học 2025 - 2026</option>
              <option>Học kỳ 1 - Năm học 2025 - 2026</option>
            </select>
            <select className="px-4 py-2 bg-surface border border-outline-variant rounded-lg text-body-md focus:outline-none focus:border-primary font-medium min-w-[200px]">
              <option>Thời khóa biểu cá nhân</option>
              <option>Thời khóa biểu lớp</option>
            </select>
          </div>
          <div className="flex gap-2">
            <button className="flex items-center gap-2 px-4 py-2 border border-outline-variant rounded-lg text-button text-error hover:bg-error/5 transition-colors">
              <span className="material-symbols-outlined text-[18px]">print</span> In
            </button>
            <button className="flex items-center gap-2 px-4 py-2 border border-outline-variant rounded-lg text-button text-error hover:bg-error/5 transition-colors">
              <span className="material-symbols-outlined text-[18px]">download</span> Xuất Excel
            </button>
          </div>
        </div>
      </div>

      <div className="bg-surface-container-lowest rounded-xl shadow-card border border-outline-variant/30 overflow-hidden">
        <div className="overflow-x-auto">
          <table className="w-full text-center border-collapse min-w-[1200px]">
            <thead className="bg-surface-container-low border-b-2 border-primary/20">
              <tr>
                <th className="py-3 px-2 font-semibold text-xs text-on-surface-variant uppercase tracking-wider">Mã MH</th>
                <th className="py-3 px-2 font-semibold text-xs text-on-surface-variant uppercase tracking-wider text-left min-w-[200px]">Tên môn học</th>
                <th className="py-3 px-2 font-semibold text-xs text-on-surface-variant uppercase tracking-wider">Nhóm tổ</th>
                <th className="py-3 px-2 font-semibold text-xs text-on-surface-variant uppercase tracking-wider">Tín chỉ</th>
                <th className="py-3 px-2 font-semibold text-xs text-on-surface-variant uppercase tracking-wider">Lớp</th>
                <th className="py-3 px-2 font-semibold text-xs text-on-surface-variant uppercase tracking-wider">Thứ</th>
                <th className="py-3 px-2 font-semibold text-xs text-on-surface-variant uppercase tracking-wider">Tiết BĐ</th>
                <th className="py-3 px-2 font-semibold text-xs text-on-surface-variant uppercase tracking-wider">Số tiết</th>
                <th className="py-3 px-2 font-semibold text-xs text-on-surface-variant uppercase tracking-wider">Phòng</th>
                <th className="py-3 px-2 font-semibold text-xs text-on-surface-variant uppercase tracking-wider">Giảng viên</th>
                <th className="py-3 px-2 font-semibold text-xs text-on-surface-variant uppercase tracking-wider">Thời gian học</th>
              </tr>
            </thead>
            <tbody className="text-sm">
              {scheduleData.map((course, index) => (
                <React.Fragment key={index}>
                  {course.schedules.map((schedule, sIndex) => (
                    <tr key={`${index}-${sIndex}`} className="border-b border-outline-variant/20 hover:bg-surface-container-lowest transition-colors">
                      {sIndex === 0 ? (
                        <>
                          <td className="py-2 px-2 font-medium text-on-surface border-r border-outline-variant/10 align-middle" rowSpan={course.schedules.length}>{course.courseCode}</td>
                          <td className="py-2 px-2 font-medium text-on-surface text-left border-r border-outline-variant/10 align-middle leading-snug" rowSpan={course.schedules.length}>{course.courseName}</td>
                          <td className="py-2 px-2 text-on-surface border-r border-outline-variant/10 align-middle" rowSpan={course.schedules.length}>{course.group}</td>
                          <td className="py-2 px-2 text-on-surface border-r border-outline-variant/10 align-middle" rowSpan={course.schedules.length}>{course.credits}</td>
                          <td className="py-2 px-2 text-on-surface border-r border-outline-variant/10 align-middle" rowSpan={course.schedules.length}>{course.className}</td>
                        </>
                      ) : null}
                      <td className="py-2 px-2 text-on-surface align-middle">{schedule.day}</td>
                      <td className="py-2 px-2 text-on-surface align-middle">{schedule.startPeriod}</td>
                      <td className="py-2 px-2 text-on-surface align-middle">{schedule.numPeriods}</td>
                      <td className="py-2 px-2 text-on-surface align-middle">{schedule.room}</td>
                      <td className="py-2 px-2 text-on-surface align-middle">{schedule.lecturer}</td>
                      <td className="py-2 px-2 text-on-surface-variant text-xs align-middle">
                        <div className="flex flex-col items-center">
                          <span>{schedule.timeRange.split(' đến ')[0]}</span>
                          <span className="text-[10px] opacity-70">đến</span>
                          <span>{schedule.timeRange.split(' đến ')[1]}</span>
                        </div>
                      </td>
                    </tr>
                  ))}
                  {/* Subtle divider between courses */}
                  {index < scheduleData.length - 1 && (
                    <tr className="border-b-2 border-primary/20"><td colSpan={11}></td></tr>
                  )}
                </React.Fragment>
              ))}
            </tbody>
          </table>
        </div>
      </div>
    </main>
  );
}
