"use client";

import React, { useState } from "react";
import Link from "next/link";
import { usePathname } from "next/navigation";
import { ThemeProvider, useTheme } from "@/components/ThemeProvider";

const navItems = [
  { href: "/", label: "Tổng quan", icon: "dashboard" },
  { href: "/classes", label: "Quản lý lớp", icon: "class" },
  { href: "/attendance", label: "Điểm danh", icon: "how_to_reg" },
  { href: "/grades", label: "Chấm điểm", icon: "grade" },
  { href: "/statistics", label: "Thống kê", icon: "query_stats" },
  { href: "/leave-requests", label: "Duyệt nghỉ phép", icon: "event_busy" },
  { href: "/chat", label: "Tin nhắn Chat", icon: "forum" },
  { href: "/messages", label: "Thông báo", icon: "campaign" },
  { href: "/settings", label: "Cài đặt", icon: "settings" },
];

const pageTitles: Record<string, string> = {
  "/": "Tổng quan giảng viên",
  "/classes": "Quản lý lớp học",
  "/attendance": "Điểm danh sinh viên",
  "/grades": "Nhập & Quản lý Điểm số",
  "/statistics": "Thống kê Chuyên cần",
  "/leave-requests": "Duyệt đơn Vắng phép",
  "/chat": "Tin nhắn Chat",
  "/messages": "Thông báo Hệ thống",
  "/settings": "Cài đặt",
  "/profile": "Hồ sơ Giảng viên",
};

function DashboardContent({ children }: { children: React.ReactNode }) {
  const pathname = usePathname();
  const { theme, toggleTheme } = useTheme();
  const [sidebarOpen, setSidebarOpen] = useState(false);
  const [profileDropdownOpen, setProfileDropdownOpen] = useState(false);
  const [notificationDropdownOpen, setNotificationDropdownOpen] = useState(false);
  return (
    <div className="bg-background text-on-surface font-inter min-h-screen flex text-body-md antialiased">
      {/* Mobile Overlay */}
      {sidebarOpen && (
        <div 
          className="fixed inset-0 bg-black/50 z-40 md:hidden backdrop-blur-sm"
          onClick={() => setSidebarOpen(false)}
        />
      )}

      {/* Sidebar */}
      <nav className={`w-[240px] h-screen fixed left-0 top-0 border-r border-outline-variant bg-surface-container flex flex-col py-6 z-50 transition-transform duration-300 md:translate-x-0 ${sidebarOpen ? 'translate-x-0' : '-translate-x-full'}`}>
        <div className="px-6 mb-8 flex flex-col">
          <h1 className="text-primary tracking-tight font-black text-2xl">UniVerse</h1>
          <p className="text-body-sm text-on-surface-variant mt-1">Hệ thống Giảng viên</p>
        </div>

        <div className="flex-1 flex flex-col gap-[4px] px-[12px] overflow-y-auto no-scrollbar">
          {navItems.map((item) => {
            const isActive = pathname === item.href || (item.href !== "/" && pathname.startsWith(item.href));
            return (
              <Link key={item.href} href={item.href}
                className={`flex items-center gap-3 px-3 py-2 rounded-lg transition-all duration-200 ${
                  isActive ? "bg-primary-container text-on-primary-container font-semibold" : "text-on-surface-variant hover:text-on-surface hover:bg-surface-container-highest"
                }`}>
                <span className="material-symbols-outlined" style={isActive ? { fontVariationSettings: "'FILL' 1" } : {}}>{item.icon}</span>
                <span className="text-button">{item.label}</span>
              </Link>
            );
          })}
        </div>

        <div className="mt-auto px-[8px]">
          <a href="#" className="flex items-center gap-3 px-3 py-2 rounded-md text-on-surface-variant hover:text-error hover:bg-error-container/20 transition-colors duration-200">
            <span className="material-symbols-outlined">logout</span>
            <span className="text-button">Đăng xuất</span>
          </a>
        </div>
      </nav>

      {/* Main */}
      <div className="flex-1 md:ml-[240px] flex flex-col min-h-screen w-full">
        <header className="fixed top-0 right-0 left-0 md:left-[240px] h-16 z-30 bg-surface-container-lowest/80 backdrop-blur-md border-b border-border-muted shadow-sm flex justify-between items-center px-4 md:px-8">
          <div className="flex items-center">
            <button
              className="md:hidden text-on-surface-variant hover:bg-surface-container p-2 rounded-full mr-2 flex items-center justify-center transition-colors"
              onClick={() => setSidebarOpen(true)}
            >
              <span className="material-symbols-outlined">menu</span>
            </button>
            <h2 className="text-h3 text-on-surface hidden sm:block">{pageTitles[pathname] || "UniVerse Lecturer"}</h2>
          </div>
          <div className="flex items-center gap-3">
            <button onClick={toggleTheme} className="text-on-surface-variant hover:bg-surface-container rounded-full p-2 transition-colors" title={theme === "light" ? "Dark Mode" : "Light Mode"}>
              <span className="material-symbols-outlined">{theme === "light" ? "dark_mode" : "light_mode"}</span>
            </button>
            <div className="relative">
              <button 
                onClick={() => setNotificationDropdownOpen(!notificationDropdownOpen)} 
                className="text-on-surface-variant hover:bg-surface-container rounded-full p-2 transition-colors relative"
              >
                <span className="material-symbols-outlined">notifications</span>
                <span className="absolute top-1 right-1 w-2.5 h-2.5 bg-error rounded-full border-2 border-surface-container-lowest"></span>
              </button>
              
              {notificationDropdownOpen && (
                <>
                  <div className="fixed inset-0 z-40" onClick={() => setNotificationDropdownOpen(false)} />
                  <div className="absolute right-0 mt-2 w-80 bg-surface-container-lowest rounded-xl shadow-lg border border-border-muted z-50 overflow-hidden flex flex-col">
                    <div className="px-4 py-3 border-b border-surface-variant flex justify-between items-center bg-surface-container-low">
                      <h3 className="font-semibold text-on-surface text-body-md">Thông báo mới</h3>
                      <button className="text-primary text-label font-medium hover:underline">Đánh dấu đã đọc</button>
                    </div>
                    <div className="max-h-80 overflow-y-auto">
                      <div className="px-4 py-3 hover:bg-surface-container cursor-pointer transition-colors border-b border-surface-variant/50">
                        <div className="flex gap-3">
                          <div className="w-8 h-8 rounded-full bg-primary-container/30 flex items-center justify-center text-primary shrink-0 mt-1">
                            <span className="material-symbols-outlined text-[18px]">event</span>
                          </div>
                          <div>
                            <p className="text-body-sm text-on-surface font-medium">Phòng đào tạo cập nhật lịch thi</p>
                            <p className="text-label text-on-surface-variant mt-0.5 line-clamp-2">Lịch thi giữa kỳ môn Cấu trúc dữ liệu và giải thuật đã được dời sang ngày 20/10.</p>
                            <p className="text-[11px] text-primary mt-1 font-medium">10 phút trước</p>
                          </div>
                        </div>
                      </div>
                      <div className="px-4 py-3 hover:bg-surface-container cursor-pointer transition-colors">
                        <div className="flex gap-3">
                          <div className="w-8 h-8 rounded-full bg-secondary-container/30 flex items-center justify-center text-secondary shrink-0 mt-1">
                            <span className="material-symbols-outlined text-[18px]">forum</span>
                          </div>
                          <div>
                            <p className="text-body-sm text-on-surface font-medium">Sinh viên Nguyễn Văn A nhắn tin</p>
                            <p className="text-label text-on-surface-variant mt-0.5 line-clamp-2">Thầy ơi cho em hỏi bài tập chương 3 phần đệ quy ạ...</p>
                            <p className="text-[11px] text-on-surface-variant mt-1">1 giờ trước</p>
                          </div>
                        </div>
                      </div>
                    </div>
                    <Link href="/messages" onClick={() => setNotificationDropdownOpen(false)} className="px-4 py-3 border-t border-surface-variant text-center text-body-sm text-primary font-medium hover:bg-surface-container transition-colors">
                      Xem tất cả thông báo
                    </Link>
                  </div>
                </>
              )}
            </div>
            <div className="w-px h-6 bg-border-muted mx-1"></div>
            <div className="relative">
              <button onClick={() => setProfileDropdownOpen(!profileDropdownOpen)} className="flex items-center gap-2 hover:bg-surface-container rounded-full pr-3 transition-colors">
                <div className="w-8 h-8 rounded-full border border-border-muted bg-tertiary-container flex items-center justify-center overflow-hidden">
                  <span className="material-symbols-outlined text-white text-[16px]">person</span>
                </div>
                <span className="text-button text-on-surface-variant hidden md:block">Giảng viên</span>
                <span className="material-symbols-outlined text-on-surface-variant text-[20px]">arrow_drop_down</span>
              </button>
              
              {profileDropdownOpen && (
                <>
                  <div className="fixed inset-0 z-40" onClick={() => setProfileDropdownOpen(false)} />
                  <div className="absolute right-0 mt-2 w-48 bg-surface-container-lowest rounded-xl shadow-lg border border-border-muted z-50 overflow-hidden flex flex-col py-1">
                    <Link href="/profile" onClick={() => setProfileDropdownOpen(false)} className="px-4 py-2 text-body-sm hover:bg-surface-container transition-colors flex items-center gap-2 text-on-surface">
                      <span className="material-symbols-outlined text-[18px]">account_circle</span>
                      Hồ sơ của tôi
                    </Link>
                    <Link href="/settings" onClick={() => setProfileDropdownOpen(false)} className="px-4 py-2 text-body-sm hover:bg-surface-container transition-colors flex items-center gap-2 text-on-surface">
                      <span className="material-symbols-outlined text-[18px]">settings</span>
                      Cài đặt
                    </Link>
                    <div className="h-px bg-border-muted my-1" />
                    <button onClick={() => setProfileDropdownOpen(false)} className="px-4 py-2 text-body-sm hover:bg-error-container/20 text-error transition-colors flex items-center gap-2 w-full text-left">
                      <span className="material-symbols-outlined text-[18px]">logout</span>
                      Đăng xuất
                    </button>
                  </div>
                </>
              )}
            </div>
          </div>
        </header>
        <div className="pt-16">{children}</div>
      </div>
    </div>
  );
}

export default function DashboardLayout({ children }: { children: React.ReactNode }) {
  return <ThemeProvider><DashboardContent>{children}</DashboardContent></ThemeProvider>;
}
