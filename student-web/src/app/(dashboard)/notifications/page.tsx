"use client";

import React, { useState } from "react";

export default function NotificationsPage() {
  const [activeTab, setActiveTab] = useState<"admin" | "teacher">("admin");

  const adminNotifications = [
    {
      id: 1,
      title: "Kế hoạch đăng ký môn học kỳ 1 năm học 2026-2027",
      date: "26/05/2026",
      content: "Phòng đào tạo thông báo kế hoạch đăng ký môn học cho học kỳ 1 năm học 2026-2027. Sinh viên vui lòng truy cập hệ thống để xem thời khóa biểu dự kiến và thời gian mở đăng ký...",
      sender: "Phòng Đào tạo",
      isRead: false
    },
    {
      id: 2,
      title: "Thông báo nộp học phí kỳ 2 năm học 2025-2026",
      date: "20/05/2026",
      content: "Nhắc nhở sinh viên hoàn thành nghĩa vụ nộp học phí kỳ 2 năm học 2025-2026 trước ngày 30/05/2026 để không bị hủy kết quả đăng ký môn học.",
      sender: "Phòng Tài chính Kế toán",
      isRead: true
    },
    {
      id: 4,
      title: "Thông báo nghỉ lễ 30/4 và 1/5",
      date: "15/04/2026",
      content: "Học viện thông báo lịch nghỉ lễ Giỗ tổ Hùng Vương, Giải phóng miền Nam 30/4 và Quốc tế lao động 1/5...",
      sender: "Phòng Đào tạo",
      isRead: true
    }
  ];

  const teacherNotifications = [
    {
      id: 3,
      title: "Cập nhật tài liệu ôn tập giữa kỳ môn Nhập môn CNPM",
      date: "18/05/2026",
      content: "Giảng viên đã tải lên tài liệu ôn tập giữa kỳ. Các em tải về và chuẩn bị cho bài kiểm tra vào tuần sau.",
      sender: "GV. Đỗ Thị Liên",
      isRead: true
    },
    {
      id: 5,
      title: "Thay đổi phòng học môn Lập trình Web",
      date: "10/05/2026",
      content: "Do sự cố máy chiếu, buổi học ngày 12/05/2026 sẽ chuyển từ phòng A2-301 sang phòng B1-104. Các em lưu ý đi học đúng phòng.",
      sender: "GV. Nguyễn Tuấn A",
      isRead: false
    }
  ];

  const notifications = activeTab === "admin" ? adminNotifications : teacherNotifications;

  return (
    <main className="flex-1 overflow-y-auto pt-24 px-xl pb-xl bg-background flex flex-col gap-lg">
      <div className="flex justify-between items-end mb-4">
        <div>
          <h1 className="font-h1 text-h1 text-on-surface mb-xs">Thông báo</h1>
          <p className="text-on-surface-variant font-body-md">Cập nhật các thông báo mới nhất từ học viện và giảng viên.</p>
        </div>
      </div>

      {/* Tabs */}
      <div className="flex gap-4 border-b border-outline-variant/50">
        <button 
          className={`pb-3 px-4 font-button text-button transition-colors border-b-2 ${activeTab === 'admin' ? 'border-primary text-primary' : 'border-transparent text-on-surface-variant hover:text-on-surface'}`}
          onClick={() => setActiveTab("admin")}
        >
          Thông báo từ Ban quản trị
        </button>
        <button 
          className={`pb-3 px-4 font-button text-button transition-colors border-b-2 ${activeTab === 'teacher' ? 'border-primary text-primary' : 'border-transparent text-on-surface-variant hover:text-on-surface'}`}
          onClick={() => setActiveTab("teacher")}
        >
          Thông báo từ Giảng viên
        </button>
      </div>

      <div className="bg-surface-container-lowest rounded-xl shadow-card border border-outline-variant/30 overflow-hidden">
        <div className="flex flex-col">
          {notifications.map((notif) => (
            <div key={notif.id} className={`p-6 border-b border-outline-variant/30 transition-colors cursor-pointer hover:bg-surface-container-lowest ${notif.isRead ? 'bg-surface' : 'bg-primary-container/10'}`}>
              <div className="flex justify-between items-start mb-2">
                <h3 className={`text-h3 font-h3 ${notif.isRead ? 'text-on-surface' : 'text-primary font-bold'}`}>
                  {notif.title}
                </h3>
                <span className="text-label font-label text-on-surface-variant whitespace-nowrap ml-4">
                  {notif.date}
                </span>
              </div>
              <div className="flex items-center gap-2 mb-3">
                <span className="material-symbols-outlined text-[16px] text-primary">{activeTab === "admin" ? "campaign" : "school"}</span>
                <span className="text-body-sm font-label font-medium text-on-surface-variant">Người gửi: {notif.sender}</span>
              </div>
              <p className="text-body-md text-on-surface line-clamp-2">
                {notif.content}
              </p>
            </div>
          ))}
          {notifications.length === 0 && (
            <div className="p-8 text-center text-on-surface-variant">
              Không có thông báo nào.
            </div>
          )}
        </div>
      </div>
    </main>
  );
}
