"use client";

import React from "react";
import Link from "next/link";

const scheduleData = [
  {
    day: "Thứ 2 (25/05)",
    classes: [
      {
        id: 1,
        time: "07:00 - 09:00",
        periods: "Tiết 1 - 2",
        name: "Nhập môn CNPM (INT1340_CLC)",
        room: "Phòng 503-A1",
        group: "01",
        lecturer: "Đỗ Thị Liên",
        status: "active"
      },
      {
        id: 2,
        time: "09:00 - 11:00",
        periods: "Tiết 3 - 4",
        name: "Lập trình Web",
        room: "Phòng 301-A2",
        group: "04",
        lecturer: "Nguyễn Văn A",
        status: "upcoming"
      }
    ]
  },
  {
    day: "Thứ 3 (26/05)",
    classes: [
      {
        id: 3,
        time: "13:00 - 15:00",
        periods: "Tiết 7 - 8",
        name: "Cơ sở dữ liệu",
        room: "Phòng 402-A3",
        group: "02",
        lecturer: "Trần Thị B",
        status: "upcoming"
      }
    ]
  }
];

export default function WeeklySchedulePage() {
  return (
    <main className="flex-1 overflow-y-auto">
      <div className="mb-lg">
        <h1 className="font-h1 text-h1 text-on-background">Thời khóa biểu tuần</h1>
        <p className="font-body-md text-body-md text-on-surface-variant mt-1">
          Lịch học chi tiết của bạn trong tuần này. Mỗi tiết học kéo dài 1 tiếng.
        </p>
      </div>

      <div className="bg-surface-container-lowest rounded-xl shadow-card border border-outline-variant/30 overflow-hidden flex flex-col p-6 mb-8">
        <div className="flex flex-col sm:flex-row gap-4 items-center">
          <select className="px-4 py-2 bg-surface border border-outline-variant rounded-lg text-body-md focus:outline-none focus:border-primary w-full sm:w-auto">
            <option>Học kỳ 2 - Năm học 2025 - 2026</option>
          </select>
          <select className="px-4 py-2 bg-surface border border-outline-variant rounded-lg text-body-md focus:outline-none focus:border-primary flex-1 w-full">
            <option>Tuần 42 [25/05/2026 - 31/05/2026]</option>
          </select>
        </div>
      </div>

      <div className="flex flex-col gap-8">
        {scheduleData.map((daySchedule, idx) => (
          <div key={idx} className="bg-surface-container-lowest rounded-xl shadow-[0px_4px_20px_rgba(26,26,26,0.05)] border border-outline-variant/30 flex flex-col overflow-hidden">
            <div className="px-6 py-4 bg-surface-container-low border-b border-outline-variant/30">
              <h3 className="font-h3 text-h3 text-on-surface flex items-center gap-2">
                <span className="material-symbols-outlined text-primary">calendar_today</span> {daySchedule.day}
              </h3>
            </div>
            <div className="p-4 flex flex-col gap-4">
              {daySchedule.classes.map((cls) => (
                <div key={cls.id} className="flex flex-col sm:flex-row gap-4 items-start sm:items-center justify-between p-4 rounded-lg bg-surface hover:bg-surface-container-low transition-colors border border-transparent hover:border-outline-variant/50">
                  <div className="flex items-start gap-4">
                    <div className="flex flex-col items-center justify-center min-w-[80px] h-16 rounded-lg bg-primary-fixed text-on-primary-fixed px-2">
                      <span className="font-h3 text-h3 leading-none">{cls.time.split(" - ")[0]}</span>
                      <span className="text-[11px] font-medium opacity-80 mt-1">{cls.periods}</span>
                    </div>
                    <div>
                      <h4 className="font-button text-button text-on-surface">{cls.name}</h4>
                      <div className="font-body-sm text-body-sm text-on-surface-variant flex flex-wrap items-center gap-x-3 gap-y-1 mt-1">
                        <span className="flex items-center gap-1">
                          <span className="material-symbols-outlined text-[16px]">location_on</span> {cls.room}
                        </span>
                        <span className="flex items-center gap-1">
                          <span className="material-symbols-outlined text-[16px]">group</span> Nhóm {cls.group}
                        </span>
                        <span className="flex items-center gap-1">
                          <span className="material-symbols-outlined text-[16px]">person</span> GV: {cls.lecturer}
                        </span>
                      </div>
                    </div>
                  </div>
                  {cls.status === 'active' && (
                    <Link href="/attendance">
                      <button className="font-button text-button text-primary bg-primary-fixed/50 px-4 py-2 rounded-lg hover:bg-primary-fixed transition-colors whitespace-nowrap self-stretch sm:self-auto flex items-center justify-center gap-2 mt-2 sm:mt-0 shadow-sm border border-primary/10">
                        <span className="material-symbols-outlined text-[18px]">how_to_reg</span> Điểm danh
                      </button>
                    </Link>
                  )}
                </div>
              ))}
            </div>
          </div>
        ))}
      </div>
    </main>
  );
}
