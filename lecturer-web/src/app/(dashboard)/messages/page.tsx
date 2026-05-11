"use client";
import React, { useState } from "react";

const selectedClasses = ["CS101 - Nhập môn CS", "SE202 - Kỹ thuật PM"];

const historyItems = [
  {
    title: "Cập nhật tài liệu ôn tập giữa kỳ",
    date: "Hôm qua, 14:30",
    recipients: "3 Lớp nhận",
    readPct: 85,
  },
  {
    title: "Thông báo nghỉ học ngày 20/11",
    date: "15/11/2023",
    recipients: "Tất cả lớp",
    readPct: 98,
  },
];

export default function MessagesPage() {
  const [title, setTitle] = useState("");
  const [content, setContent] = useState("");

  return (
    <main className="flex-1 overflow-y-auto p-xl bg-background flex flex-col gap-lg">
      {/* Header */}
      <div className="flex justify-between items-end">
        <div>
          <h1 className="font-h1 text-h1 text-on-surface mb-xs">Tạo thông báo mới</h1>
          <p className="font-body-md text-body-md text-on-surface-variant">Gửi tin nhắn quan trọng đến các lớp học của bạn</p>
        </div>
      </div>

      {/* Bento Grid */}
      <div className="grid grid-cols-1 xl:grid-cols-12 gap-lg">
        {/* Left: Form + History */}
        <div className="xl:col-span-8 flex flex-col gap-lg">
          {/* Drafting Card */}
          <div className="bg-surface-container-lowest rounded-xl shadow-sm border border-[#EEEDFE] p-lg">
            <h2 className="font-h2 text-h2 text-on-surface mb-md">Soạn thảo</h2>
            <form className="space-y-md">
              {/* Class selector */}
              <div>
                <label className="font-label text-label text-on-surface-variant block mb-sm uppercase">Gửi đến (Chọn lớp)</label>
                <div className="relative">
                  <div className="flex flex-wrap gap-2 p-2 min-h-[48px] bg-white border border-outline-variant rounded-lg items-center focus-within:border-primary focus-within:ring-1 focus-within:ring-primary transition-colors">
                    {selectedClasses.map(cls => (
                      <div key={cls} className="bg-primary-fixed text-primary px-3 py-1 rounded-md text-sm flex items-center gap-1 font-medium">
                        {cls}
                        <span className="material-symbols-outlined text-[16px] cursor-pointer hover:text-on-primary-fixed">close</span>
                      </div>
                    ))}
                    <input className="flex-1 border-none focus:ring-0 text-sm p-0 min-w-[120px] outline-none" placeholder="Tìm kiếm lớp..." type="text" />
                  </div>
                  <span className="material-symbols-outlined absolute right-3 top-3 text-outline">arrow_drop_down</span>
                </div>
              </div>

              {/* Title */}
              <div>
                <label className="font-label text-label text-on-surface-variant block mb-sm uppercase">Tiêu đề thông báo</label>
                <input
                  value={title}
                  onChange={e => setTitle(e.target.value)}
                  className="w-full bg-white border border-outline-variant rounded-lg px-4 py-3 text-body-lg text-on-surface focus:border-primary focus:ring-1 focus:ring-primary outline-none transition-colors placeholder:text-outline"
                  placeholder="Nhập tiêu đề (VD: Thay đổi lịch học tuần này)"
                />
              </div>

              {/* Content with toolbar */}
              <div>
                <label className="font-label text-label text-on-surface-variant block mb-sm uppercase">Nội dung</label>
                <div className="border border-outline-variant rounded-lg overflow-hidden flex flex-col focus-within:border-primary focus-within:ring-1 focus-within:ring-primary transition-colors">
                  <div className="bg-surface-container-low border-b border-outline-variant px-2 py-2 flex items-center gap-1 flex-wrap">
                    {[
                      { icon: "format_bold" }, { icon: "format_italic" }, { icon: "format_underlined" },
                    ].map(b => (
                      <button key={b.icon} type="button" className="p-1.5 rounded hover:bg-surface-variant text-on-surface-variant transition-colors">
                        <span className="material-symbols-outlined text-[20px]">{b.icon}</span>
                      </button>
                    ))}
                    <div className="w-px h-5 bg-outline-variant mx-1" />
                    {[{ icon: "format_list_bulleted" }, { icon: "format_list_numbered" }].map(b => (
                      <button key={b.icon} type="button" className="p-1.5 rounded hover:bg-surface-variant text-on-surface-variant transition-colors">
                        <span className="material-symbols-outlined text-[20px]">{b.icon}</span>
                      </button>
                    ))}
                    <div className="w-px h-5 bg-outline-variant mx-1" />
                    {[{ icon: "link" }, { icon: "attach_file" }].map(b => (
                      <button key={b.icon} type="button" className="p-1.5 rounded hover:bg-surface-variant text-on-surface-variant transition-colors">
                        <span className="material-symbols-outlined text-[20px]">{b.icon}</span>
                      </button>
                    ))}
                  </div>
                  <textarea
                    rows={6}
                    value={content}
                    onChange={e => setContent(e.target.value)}
                    className="w-full border-none focus:ring-0 p-4 text-body-lg text-on-surface resize-y min-h-[150px] outline-none placeholder:text-outline"
                    placeholder="Nhập nội dung thông báo tại đây..."
                  />
                </div>
              </div>

              {/* Actions */}
              <div className="flex justify-end gap-3 pt-2">
                <button type="button" className="px-6 py-2 rounded-lg font-button text-button text-primary bg-primary-fixed hover:bg-primary-fixed-dim transition-colors">
                  Lưu nháp
                </button>
                <button type="button" className="px-6 py-2 rounded-lg font-button text-button text-on-primary bg-primary hover:opacity-90 flex items-center gap-2 transition-colors shadow-sm">
                  <span className="material-symbols-outlined text-[18px]">send</span>Gửi thông báo
                </button>
              </div>
            </form>
          </div>

          {/* History Card */}
          <div className="bg-surface-container-lowest rounded-xl shadow-sm border border-[#EEEDFE] p-lg">
            <div className="flex justify-between items-center mb-md">
              <h2 className="font-h3 text-h3 text-on-surface">Lịch sử đã gửi</h2>
              <button className="text-sm font-medium text-primary hover:underline">Xem tất cả</button>
            </div>
            <div className="space-y-4">
              {historyItems.map((item, i) => (
                <div key={i} className="flex flex-col sm:flex-row gap-4 justify-between p-4 rounded-lg hover:bg-[#F8F7FF] transition-colors border border-transparent hover:border-[#EEEDFE]">
                  <div className="flex-1">
                    <h4 className="font-medium text-on-surface mb-1">{item.title}</h4>
                    <div className="flex items-center gap-3 text-xs text-on-surface-variant">
                      <span className="flex items-center gap-1">
                        <span className="material-symbols-outlined text-[14px]">calendar_today</span>{item.date}
                      </span>
                      <span className="flex items-center gap-1">
                        <span className="material-symbols-outlined text-[14px]">groups</span>{item.recipients}
                      </span>
                    </div>
                  </div>
                  <div className="sm:w-48 flex flex-col justify-center">
                    <div className="flex justify-between text-xs mb-1">
                      <span className="text-on-surface-variant">Đã đọc</span>
                      <span className="font-medium text-secondary">{item.readPct}%</span>
                    </div>
                    <div className="w-full bg-surface-variant rounded-full h-1.5 overflow-hidden">
                      <div className="bg-secondary h-1.5 rounded-full" style={{ width: `${item.readPct}%` }} />
                    </div>
                  </div>
                </div>
              ))}
            </div>
          </div>
        </div>

        {/* Right: Phone Preview */}
        <div className="xl:col-span-4">
          <div className="bg-surface-container-lowest rounded-xl shadow-sm border border-[#EEEDFE] p-lg sticky top-24">
            <div className="flex items-center gap-2 mb-md">
              <span className="material-symbols-outlined text-primary">smartphone</span>
              <h2 className="font-h3 text-h3 text-on-surface">Xem trước hiển thị</h2>
            </div>
            {/* Phone Frame */}
            <div className="mx-auto w-[280px] h-[580px] bg-black rounded-[36px] p-2 shadow-xl relative overflow-hidden ring-1 ring-outline-variant/50">
              <div className="absolute top-0 left-1/2 -translate-x-1/2 w-32 h-6 bg-black rounded-b-2xl z-20" />
              <div className="bg-background w-full h-full rounded-[28px] overflow-hidden flex flex-col relative">
                {/* Status bar */}
                <div className="h-12 pt-3 px-6 flex justify-between items-center text-[10px] font-medium text-on-surface z-10">
                  <span>9:41</span>
                  <div className="flex gap-1 items-center">
                    <span className="material-symbols-outlined text-[12px]">signal_cellular_4_bar</span>
                    <span className="material-symbols-outlined text-[12px]">wifi</span>
                    <span className="material-symbols-outlined text-[12px]">battery_full</span>
                  </div>
                </div>
                {/* App header */}
                <div className="px-4 pb-3 border-b border-outline-variant/30 flex items-center gap-3">
                  <span className="material-symbols-outlined text-[20px] text-on-surface">arrow_back</span>
                  <span className="font-semibold text-sm">Chi tiết thông báo</span>
                </div>
                {/* Preview content */}
                <div className="flex-1 p-4 overflow-y-auto">
                  <div className="flex items-center gap-3 mb-4">
                    <div className="w-10 h-10 rounded-full bg-primary-fixed flex items-center justify-center shrink-0">
                      <span className="material-symbols-outlined text-primary text-[20px]">school</span>
                    </div>
                    <div>
                      <div className="text-sm font-semibold text-on-surface leading-tight">GV. Nguyễn Văn A</div>
                      <div className="text-[11px] text-on-surface-variant">Vừa xong • Lớp CS101, SE202</div>
                    </div>
                  </div>
                  <h3 className="text-[16px] font-bold text-on-surface mb-3 leading-snug">
                    {title ? (
                      <span>{title}</span>
                    ) : (
                      <span className="text-on-surface-variant/50 italic">Tiêu đề thông báo sẽ hiển thị ở đây...</span>
                    )}
                  </h3>
                  <div className="text-[13px] text-on-surface-variant leading-relaxed">
                    {content ? (
                      <p>{content}</p>
                    ) : (
                      <p className="text-on-surface-variant/50 italic">Nội dung chi tiết của thông báo sẽ được hiển thị tại khu vực này. Trình bày rõ ràng giúp sinh viên dễ dàng nắm bắt thông tin quan trọng từ giảng viên...</p>
                    )}
                  </div>
                  <div className="mt-5 p-3 rounded-lg border border-outline-variant/40 flex items-center gap-3 bg-surface-container-lowest">
                    <div className="w-8 h-8 rounded bg-error-container text-on-error-container flex items-center justify-center shrink-0">
                      <span className="material-symbols-outlined text-[16px]">picture_as_pdf</span>
                    </div>
                    <div className="flex-1 min-w-0">
                      <div className="text-[12px] font-medium truncate">Tailieu_Huongdan.pdf</div>
                      <div className="text-[10px] text-on-surface-variant">2.4 MB</div>
                    </div>
                  </div>
                </div>
              </div>
            </div>
            <p className="text-center text-xs text-on-surface-variant mt-4">Hiển thị có thể khác biệt nhẹ trên các thiết bị khác nhau.</p>
          </div>
        </div>
      </div>
    </main>
  );
}
