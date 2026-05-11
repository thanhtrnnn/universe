"use client";
import React, { useState } from "react";

const requests = [
  {
    id: 1,
    name: "Trần Thị Mai",
    mssv: "SV2021001",
    khoa: "Khóa 21",
    lop: "SE102.N11",
    date: "15/10/2023",
    reason: "Sốt cao không giảm, xin phép nghỉ 1 buổi học thực hành. Đã đi khám tại bệnh viện quận.",
    hasImage: true,
    initials: "",
  },
  {
    id: 2,
    name: "Lê Đức Phát",
    mssv: "SV2021088",
    khoa: "Khóa 21",
    lop: "IT001.O22",
    date: "16/10/2023 - 17/10/2023",
    reason: "Gia đình có việc đột xuất ở quê, cần về gấp trong 2 ngày. Có xác nhận của phụ huynh kèm theo.",
    hasImage: false,
    initials: "",
  },
  {
    id: 3,
    name: "Nguyễn Thị Hoa",
    mssv: "SV2021156",
    khoa: "Khóa 21",
    lop: "CS106.P13",
    date: "18/10/2023",
    reason: "Không có lý do chi tiết. Chỉ đính kèm đơn.",
    hasImage: true,
    initials: "NH",
  },
];

export default function LeaveRequestsPage() {
  const [tab, setTab] = useState<"pending" | "history">("pending");

  return (
    <main className="flex-1 overflow-y-auto p-xl bg-background flex flex-col gap-lg">
      <div className="flex flex-col gap-md">
        <h1 className="font-h1 text-h1 text-on-background">Duyệt đơn vắng phép</h1>

        {/* Quick Stats */}
        <div className="grid grid-cols-3 gap-md">
          <div className="bg-primary-container text-on-primary-container p-md rounded-xl flex items-center gap-4">
            <div className="bg-white/20 p-3 rounded-full flex items-center justify-center">
              <span className="material-symbols-outlined" style={{ fontVariationSettings: "'FILL' 1" }}>pending_actions</span>
            </div>
            <div>
              <div className="font-h2 text-h2">12</div>
              <div className="font-body-sm text-body-sm opacity-80">Đơn chờ duyệt</div>
            </div>
          </div>
          <div className="bg-surface-container-lowest border border-outline-variant p-md rounded-xl shadow-sm flex items-center gap-4">
            <div className="bg-secondary-container text-on-secondary-container p-3 rounded-full flex items-center justify-center">
              <span className="material-symbols-outlined" style={{ fontVariationSettings: "'FILL' 1" }}>check_circle</span>
            </div>
            <div>
              <div className="font-h2 text-h2 text-on-surface">45</div>
              <div className="font-body-sm text-body-sm text-on-surface-variant">Đã duyệt tuần này</div>
            </div>
          </div>
          <div className="bg-surface-container-lowest border border-outline-variant p-md rounded-xl shadow-sm flex items-center gap-4">
            <div className="bg-error-container text-on-error-container p-3 rounded-full flex items-center justify-center">
              <span className="material-symbols-outlined" style={{ fontVariationSettings: "'FILL' 1" }}>cancel</span>
            </div>
            <div>
              <div className="font-h2 text-h2 text-on-surface">3</div>
              <div className="font-body-sm text-body-sm text-on-surface-variant">Đã từ chối tuần này</div>
            </div>
          </div>
        </div>

        {/* Tabs */}
        <div className="flex border-b border-outline-variant">
          <button
            onClick={() => setTab("pending")}
            className={`px-md py-sm font-button text-button mr-4 border-b-2 transition-colors ${tab === "pending" ? "text-primary border-primary" : "text-on-surface-variant border-transparent hover:text-on-surface"}`}>
            Chờ duyệt
          </button>
          <button
            onClick={() => setTab("history")}
            className={`px-md py-sm font-button text-button border-b-2 transition-colors ${tab === "history" ? "text-primary border-primary" : "text-on-surface-variant border-transparent hover:text-on-surface"}`}>
            Lịch sử xử lý
          </button>
        </div>
      </div>

      {/* Request Cards */}
      <div className="flex flex-col gap-md">
        {requests.map(req => (
          <div key={req.id} className="bg-surface-container-lowest rounded-xl shadow-sm border border-outline-variant p-lg flex flex-col md:flex-row gap-lg items-start md:items-center transition-all hover:shadow-md">
            {/* Student Info */}
            <div className="flex items-center gap-4 w-full md:w-1/4">
              <div className="w-12 h-12 rounded-full overflow-hidden bg-surface-variant shrink-0 flex items-center justify-center text-primary font-h3">
                {req.initials ? (
                  <span>{req.initials}</span>
                ) : (
                  <span className="material-symbols-outlined">person</span>
                )}
              </div>
              <div>
                <div className="font-h3 text-h3 text-on-surface line-clamp-1">{req.name}</div>
                <div className="font-body-sm text-body-sm text-on-surface-variant">{req.mssv} • {req.khoa}</div>
              </div>
            </div>

            {/* Class & Date */}
            <div className="w-full md:w-1/5 flex flex-col gap-1">
              <div className="font-button text-button text-on-surface flex items-center gap-2">
                <span className="material-symbols-outlined text-[18px] text-tertiary">class</span>
                {req.lop}
              </div>
              <div className="font-body-sm text-body-sm text-on-surface-variant flex items-center gap-2">
                <span className="material-symbols-outlined text-[16px]">calendar_today</span>
                {req.date}
              </div>
            </div>

            {/* Reason & Evidence */}
            <div className="w-full md:flex-1 flex gap-4 bg-surface rounded-lg p-sm border border-surface-variant">
              <div className="flex-1">
                <div className="font-label text-label text-on-surface-variant mb-1 uppercase">Lý do</div>
                <div className={`font-body-md text-body-md text-on-surface line-clamp-2 ${!req.reason.includes("Không") ? "" : "italic text-on-surface-variant"}`}>
                  {req.reason}
                </div>
              </div>
              <div className="w-16 h-16 rounded overflow-hidden shrink-0 cursor-pointer border border-outline-variant relative group bg-surface-variant flex items-center justify-center">
                {req.hasImage ? (
                  <span className="material-symbols-outlined text-outline text-2xl">image</span>
                ) : (
                  <span className="material-symbols-outlined text-outline">description</span>
                )}
                <div className="absolute inset-0 bg-black/40 flex items-center justify-center opacity-0 group-hover:opacity-100 transition-opacity">
                  <span className="material-symbols-outlined text-white">zoom_in</span>
                </div>
              </div>
            </div>

            {/* Actions */}
            <div className="w-full md:w-auto flex flex-row md:flex-col gap-sm shrink-0">
              <button className="flex-1 md:w-full bg-primary text-on-primary font-button text-button px-md py-2 rounded-lg flex items-center justify-center gap-2 hover:opacity-90 transition-colors">
                <span className="material-symbols-outlined text-[18px]">check</span>Chấp nhận
              </button>
              <button className="flex-1 md:w-full bg-error-container text-on-error-container font-button text-button px-md py-2 rounded-lg flex items-center justify-center gap-2 hover:bg-[#ffcdc7] transition-colors">
                <span className="material-symbols-outlined text-[18px]">close</span>Từ chối
              </button>
            </div>
          </div>
        ))}
      </div>

      {/* Pagination */}
      <div className="flex justify-between items-center mt-4">
        <span className="font-body-sm text-body-sm text-on-surface-variant">Hiển thị 1-3 trong số 12 đơn</span>
        <div className="flex gap-2">
          <button className="p-2 border border-outline-variant rounded-lg text-on-surface-variant hover:bg-surface-variant opacity-50" disabled>
            <span className="material-symbols-outlined">chevron_left</span>
          </button>
          <button className="p-2 border border-outline-variant rounded-lg text-on-surface hover:bg-surface-variant">
            <span className="material-symbols-outlined">chevron_right</span>
          </button>
        </div>
      </div>
    </main>
  );
}
