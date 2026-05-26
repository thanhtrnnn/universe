"use client";

import React, { useState } from "react";
import Link from "next/link";

interface Alert {
  id: string;
  title: string;
  message: string;
  severity: "error" | "warning" | "info";
  time: string;
  isRead: boolean;
  date: string;
}

const mockAlerts: Alert[] = [
  { id: "1", title: "Lớp CS101 điểm danh thấp", message: "Tỷ lệ chuyên cần dưới 60% trong 3 buổi liên tiếp. Cần liên hệ với giảng viên để kiểm tra.", severity: "error", time: "10:30 AM", isRead: false, date: "2026-05-26" },
  { id: "2", title: "Máy chiếu phòng P.302 hỏng", message: "Giảng viên báo cáo máy chiếu không lên nguồn. Cần bộ phận IT kiểm tra gấp.", severity: "error", time: "08:15 AM", isRead: false, date: "2026-05-26" },
  { id: "3", title: "Tải CPU Server đang cao", message: "Server quản lý điểm danh đang chạy ở mức 90% CPU.", severity: "warning", time: "Hôm qua", isRead: true, date: "2026-05-25" },
  { id: "4", title: "Bảo trì hệ thống", message: "Lên lịch bảo trì server vào 02:00 AM Chủ Nhật tuần này.", severity: "info", time: "Hôm qua", isRead: true, date: "2026-05-25" },
  { id: "5", title: "Cập nhật phần mềm thành công", message: "Phiên bản v2.1.0 đã được deploy thành công.", severity: "info", time: "2 ngày trước", isRead: true, date: "2026-05-24" },
  { id: "6", title: "Phát hiện đăng nhập bất thường", message: "Tài khoản admin_02 đăng nhập từ IP lạ lúc 03:00 AM.", severity: "warning", time: "2 ngày trước", isRead: true, date: "2026-05-24" },
];

export default function AlertsPage() {
  const [alerts, setAlerts] = useState<Alert[]>(mockAlerts);
  const [search, setSearch] = useState("");
  const [filterSeverity, setFilterSeverity] = useState("all");

  const getSeverityStyle = (severity: string) => {
    switch (severity) {
      case "error": return { bg: "bg-error-container", text: "text-error", icon: "warning", border: "border-error-container" };
      case "warning": return { bg: "bg-secondary-container/50", text: "text-secondary", icon: "report_problem", border: "border-secondary-container/50" };
      case "info": return { bg: "bg-primary-container/30", text: "text-primary", icon: "info", border: "border-primary-container/30" };
      default: return { bg: "bg-surface-container", text: "text-on-surface", icon: "notifications", border: "border-border-muted" };
    }
  };

  const getSeverityLabel = (severity: string) => {
    switch (severity) {
      case "error": return "Nghiêm trọng";
      case "warning": return "Cảnh báo";
      case "info": return "Thông tin";
      default: return "";
    }
  };

  const markAsRead = (id: string) => {
    setAlerts(alerts.map(a => a.id === id ? { ...a, isRead: true } : a));
  };

  const deleteAlert = (id: string) => {
    setAlerts(alerts.filter(a => a.id !== id));
  };

  const markAllAsRead = () => {
    setAlerts(alerts.map(a => ({ ...a, isRead: true })));
  };

  const filteredAlerts = alerts.filter(a => {
    const matchesSearch = a.title.toLowerCase().includes(search.toLowerCase()) || a.message.toLowerCase().includes(search.toLowerCase());
    const matchesSeverity = filterSeverity === "all" || a.severity === filterSeverity;
    return matchesSearch && matchesSeverity;
  });

  const unreadCount = alerts.filter(a => !a.isRead).length;

  return (
    <main className="flex-1 overflow-y-auto mt-16 p-xl bg-background min-h-screen">
      {/* Header */}
      <div className="flex flex-col md:flex-row justify-between items-start md:items-end mb-lg gap-4">
        <div>
          <Link href="/" className="inline-flex items-center gap-sm text-on-surface-variant hover:text-primary transition-colors duration-200 mb-4 font-button text-button">
            <span className="material-symbols-outlined text-[20px]">arrow_back</span>
            Trở về Bảng điều khiển
          </Link>
          <div className="flex items-center gap-3">
            <h2 className="text-h1 text-on-surface">Cảnh báo hệ thống</h2>
            {unreadCount > 0 && (
              <span className="bg-error text-on-error px-2 py-0.5 rounded-full text-label font-bold">
                {unreadCount} chưa đọc
              </span>
            )}
          </div>
          <p className="text-body-md text-on-surface-variant mt-1">
            Theo dõi, xử lý các sự cố và thông báo quan trọng từ hệ thống
          </p>
        </div>
        <div className="flex gap-2">
          <button 
            onClick={markAllAsRead}
            disabled={unreadCount === 0}
            className="bg-surface-container-highest hover:bg-surface-dim text-on-surface text-button px-4 py-2 rounded-lg flex items-center gap-2 shadow-sm transition-colors duration-200 disabled:opacity-50"
          >
            <span className="material-symbols-outlined text-[20px]">done_all</span>
            Đánh dấu đọc tất cả
          </button>
        </div>
      </div>

      {/* Toolbar */}
      <div className="bg-surface-container-lowest rounded-xl shadow-[var(--shadow-card)] border border-border-muted p-md mb-lg flex flex-col md:flex-row gap-4 justify-between items-center">
        <div className="relative w-full md:w-96">
          <span className="material-symbols-outlined absolute left-3 top-1/2 -translate-y-1/2 text-on-surface-variant text-[20px]">search</span>
          <input
            className="w-full pl-10 pr-4 py-2 bg-surface rounded-lg border border-outline-variant focus:border-primary focus:ring-1 focus:ring-primary outline-none transition-all text-body-sm text-on-surface placeholder:text-on-surface-variant/70"
            placeholder="Tìm kiếm nội dung cảnh báo..."
            type="text"
            value={search}
            onChange={(e) => setSearch(e.target.value)}
          />
        </div>
        
        <div className="flex gap-3 w-full md:w-auto">
          <div className="relative flex-1 md:w-48">
            <select 
              value={filterSeverity} 
              onChange={e => setFilterSeverity(e.target.value)}
              className="w-full appearance-none bg-surface border border-outline-variant rounded-lg px-4 py-2 text-body-sm focus:border-primary outline-none transition-colors cursor-pointer"
            >
              <option value="all">Tất cả mức độ</option>
              <option value="error">Nghiêm trọng</option>
              <option value="warning">Cảnh báo</option>
              <option value="info">Thông tin</option>
            </select>
            <span className="material-symbols-outlined absolute right-3 top-1/2 -translate-y-1/2 text-outline-variant pointer-events-none text-[20px]">expand_more</span>
          </div>
          <button className="flex items-center justify-center p-2 border border-outline-variant rounded-lg text-on-surface-variant hover:bg-surface-container-highest hover:text-primary transition-colors" title="Lọc theo ngày">
            <span className="material-symbols-outlined">calendar_month</span>
          </button>
        </div>
      </div>

      {/* Alerts List */}
      <div className="space-y-4">
        {filteredAlerts.length > 0 ? (
          filteredAlerts.map(alert => {
            const styles = getSeverityStyle(alert.severity);
            return (
              <div 
                key={alert.id} 
                className={`bg-surface-container-lowest rounded-xl shadow-[var(--shadow-card)] border ${alert.isRead ? 'border-border-muted opacity-80' : `border-l-4 border-l-${alert.severity === 'error' ? 'error' : alert.severity === 'warning' ? 'secondary' : 'primary'} border-border-muted`} p-lg flex flex-col md:flex-row gap-4 md:gap-6 items-start transition-all hover:shadow-[var(--shadow-float)] hover:opacity-100 group`}
              >
                <div className={`p-3 rounded-xl ${styles.bg} ${styles.text} flex-shrink-0`}>
                  <span className="material-symbols-outlined text-[28px]">{styles.icon}</span>
                </div>
                
                <div className="flex-1 min-w-0">
                  <div className="flex items-start justify-between gap-4 mb-2">
                    <div>
                      <div className="flex items-center gap-2 mb-1">
                        <h3 className={`text-h3 ${alert.isRead ? 'text-on-surface-variant' : 'text-on-surface'}`}>
                          {alert.title}
                        </h3>
                        {!alert.isRead && (
                          <span className="w-2 h-2 rounded-full bg-primary flex-shrink-0"></span>
                        )}
                      </div>
                      <div className="flex items-center gap-3">
                        <span className={`inline-flex px-2 py-0.5 rounded text-[10px] font-bold uppercase tracking-wider ${styles.bg} ${styles.text}`}>
                          {getSeverityLabel(alert.severity)}
                        </span>
                        <span className="text-body-sm text-outline flex items-center gap-1">
                          <span className="material-symbols-outlined text-[14px]">schedule</span>
                          {alert.time} • {alert.date}
                        </span>
                      </div>
                    </div>
                  </div>
                  <p className="text-body-md text-on-surface-variant mt-3 line-clamp-2 md:line-clamp-none">
                    {alert.message}
                  </p>
                </div>

                <div className="flex md:flex-col gap-2 w-full md:w-auto mt-4 md:mt-0 justify-end md:opacity-0 group-hover:opacity-100 transition-opacity">
                  {!alert.isRead && (
                    <button 
                      onClick={() => markAsRead(alert.id)}
                      className="px-3 py-1.5 bg-surface-container hover:bg-surface-container-highest text-on-surface text-button rounded-lg transition-colors flex items-center justify-center gap-1 flex-1 md:flex-none"
                    >
                      <span className="material-symbols-outlined text-[18px]">check</span>
                      Đã xử lý
                    </button>
                  )}
                  <button 
                    onClick={() => deleteAlert(alert.id)}
                    className="px-3 py-1.5 border border-outline-variant hover:bg-error-container/20 hover:text-error hover:border-error-container/50 text-on-surface-variant text-button rounded-lg transition-colors flex items-center justify-center gap-1 flex-1 md:flex-none"
                  >
                    <span className="material-symbols-outlined text-[18px]">delete</span>
                    Xóa
                  </button>
                </div>
              </div>
            );
          })
        ) : (
          <div className="bg-surface-container-lowest rounded-xl border border-dashed border-outline-variant p-xl flex flex-col items-center justify-center text-center">
            <span className="material-symbols-outlined text-[48px] text-outline mb-4">check_circle</span>
            <h3 className="text-h3 text-on-surface mb-2">Tất cả đều ổn!</h3>
            <p className="text-body-md text-on-surface-variant">Không tìm thấy cảnh báo nào khớp với bộ lọc của bạn.</p>
          </div>
        )}
      </div>
    </main>
  );
}
