"use client";

import React, { useState } from "react";
import Link from "next/link";
import { usePathname } from "next/navigation";
import { ThemeProvider, useTheme } from "@/components/ThemeProvider";
import NotificationBell from "@/components/NotificationBell";

type NavGroup = {
  title?: string;
  items: { href: string; label: string; icon: string }[];
};

const navGroups: NavGroup[] = [
  {
    items: [
      { href: "/", label: "Bảng điều khiển", icon: "dashboard" },
      { href: "/users", label: "Người dùng", icon: "group" },
    ]
  },
  {
    title: "ĐÀO TẠO",
    items: [
      { href: "/departments", label: "Khoa & Ngành", icon: "account_balance" },
      { href: "/courses", label: "Môn học", icon: "library_books" },
      { href: "/classes", label: "Lớp học phần", icon: "school" },
      { href: "/rooms", label: "Phòng học", icon: "meeting_room" },
    ]
  },
  {
    title: "LỊCH BIỂU",
    items: [
      { href: "/schedules", label: "Thời khóa biểu", icon: "calendar_month" },
    ]
  },
  {
    title: "BÁO CÁO & GIAO TIẾP",
    items: [
      { href: "/statistics", label: "Thống kê", icon: "analytics" },
      { href: "/notifications", label: "Thông báo", icon: "campaign" },
    ]
  },
  {
    title: "HỆ THỐNG",
    items: [
      { href: "/settings", label: "Cài đặt", icon: "settings" },
    ]
  }
];

const pageTitles: Record<string, string> = {
  "/": "Tổng quan hệ thống",
  "/users": "Quản lý người dùng",
  "/users/new": "Thêm Người dùng mới",
  "/departments": "Quản lý Khoa & Ngành",
  "/courses": "Quản lý Môn học",
  "/classes": "Quản lý Lớp học",
  "/rooms": "Quản lý Phòng học",
  "/schedules": "Thời khóa biểu",
  "/statistics": "Thống kê & Báo cáo",
  "/notifications": "Gửi thông báo hệ thống",
  "/chat": "Tin nhắn",
  "/settings": "Cài đặt",
};

function DashboardContent({ children }: { children: React.ReactNode }) {
  const pathname = usePathname();
  const { theme, toggleTheme } = useTheme();
  const [sidebarOpen, setSidebarOpen] = useState(false);
  const [profileDropdownOpen, setProfileDropdownOpen] = useState(false);

  const getPageTitle = () => {
    if (pageTitles[pathname]) return pageTitles[pathname];
    if (pathname.startsWith("/classes/")) return "Chi tiết Lớp học";
    if (pathname.startsWith("/users/")) return "Thêm Người dùng mới";
    return "UniVerse Admin";
  };

  return (
    <div className="bg-background text-on-surface font-inter min-h-screen flex">
      {/* Mobile Overlay */}
      {sidebarOpen && (
        <div 
          className="fixed inset-0 bg-black/50 z-40 md:hidden backdrop-blur-sm"
          onClick={() => setSidebarOpen(false)}
        />
      )}

      {/* ===== SIDEBAR ===== */}
      <nav className={`w-[240px] h-screen fixed left-0 top-0 border-r border-border-muted bg-surface-container-lowest shadow-sm flex flex-col py-6 z-50 transition-transform duration-300 md:translate-x-0 ${sidebarOpen ? 'translate-x-0' : '-translate-x-full'}`}>
        {/* Brand */}
        <div className="px-6 mb-8 flex items-center gap-3">
          <div className="w-8 h-8 rounded-full bg-primary flex items-center justify-center shadow-sm">
            <span className="material-symbols-outlined text-on-primary text-[18px]">school</span>
          </div>
          <div>
            <h1 className="text-h2 text-on-surface tracking-tight" style={{ fontSize: '18px', lineHeight: '24px' }}>UniVerse</h1>
            <p className="text-body-sm text-on-surface-variant">Hệ thống quản trị</p>
          </div>
        </div>

        {/* Navigation Links */}
        <div className="flex-1 flex flex-col gap-2 px-[8px] overflow-y-auto overflow-x-hidden custom-scrollbar">
          {navGroups.map((group, gIdx) => (
            <div key={gIdx} className={group.title ? "mt-2" : ""}>
              {group.title && (
                <div className="px-3 mb-1 text-[11px] font-bold text-on-surface-variant/70 tracking-wider">
                  {group.title}
                </div>
              )}
              <div className="flex flex-col gap-[4px]">
                {group.items.map((item) => {
                  const isActive =
                    pathname === item.href ||
                    (item.href !== "/" && pathname.startsWith(item.href));
                  return (
                    <Link
                      key={item.href}
                      href={item.href}
                      className={`flex items-center gap-3 px-3 py-2 rounded-md transition-colors duration-200 ${
                        isActive
                          ? "bg-primary-container text-on-primary-container border-l-4 border-primary font-semibold"
                          : "text-on-surface-variant hover:text-on-surface hover:bg-surface-container"
                      }`}
                    >
                      <span
                        className="material-symbols-outlined"
                        style={isActive ? { fontVariationSettings: "'FILL' 1" } : {}}
                      >
                        {item.icon}
                      </span>
                      <span className="text-button">{item.label}</span>
                    </Link>
                  );
                })}
              </div>
            </div>
          ))}
        </div>

        {/* Footer */}
        <div className="mt-auto px-[8px]">
          <Link
            href="/login"
            className="flex items-center gap-3 px-3 py-2 rounded-md text-on-surface-variant hover:text-error hover:bg-error-container/50 transition-colors duration-200"
          >
            <span className="material-symbols-outlined">logout</span>
            <span className="text-button">Đăng xuất</span>
          </Link>
        </div>
      </nav>

      {/* ===== MAIN CONTENT AREA ===== */}
      <div className="flex-1 md:ml-[240px] flex flex-col min-h-screen w-full">
        {/* ===== TOP NAV BAR ===== */}
        <header className="fixed top-0 right-0 left-0 md:left-[240px] h-16 z-30 bg-surface-container-lowest/80 backdrop-blur-md border-b border-border-muted shadow-sm flex justify-between items-center px-4 md:px-8">
          {/* Left: Page Title & Hamburger */}
          <div className="flex items-center">
            <button
              className="md:hidden text-on-surface-variant hover:bg-surface-container p-2 rounded-full mr-2 flex items-center justify-center transition-colors"
              onClick={() => setSidebarOpen(true)}
            >
              <span className="material-symbols-outlined">menu</span>
            </button>
            <h2 className="text-h3 text-on-surface hidden sm:block">{getPageTitle()}</h2>
          </div>

          {/* Right: Actions & Profile */}
          <div className="flex items-center gap-3">
            {/* Dark mode toggle */}
            <button
              onClick={toggleTheme}
              className="text-on-surface-variant hover:bg-surface-container rounded-full p-2 transition-colors focus:ring-2 focus:ring-primary/20"
              title={theme === "light" ? "Chuyển sang Dark Mode" : "Chuyển sang Light Mode"}
            >
              <span className="material-symbols-outlined">
                {theme === "light" ? "dark_mode" : "light_mode"}
              </span>
            </button>

            <NotificationBell />

            <div className="w-px h-6 bg-border-muted mx-1"></div>

            <div className="relative">
              <button onClick={() => setProfileDropdownOpen(!profileDropdownOpen)} className="flex items-center gap-2 hover:bg-surface-container rounded-full pr-3 transition-colors focus:ring-2 focus:ring-primary/20">
                <div className="w-8 h-8 rounded-full border border-border-muted bg-primary-container flex items-center justify-center overflow-hidden">
                  <span className="material-symbols-outlined text-on-primary-container text-[16px]">person</span>
                </div>
                <span className="text-button text-on-surface-variant hidden md:block">Admin</span>
                <span className="material-symbols-outlined text-on-surface-variant text-[20px]">arrow_drop_down</span>
              </button>
              
              {profileDropdownOpen && (
                <>
                  <div className="fixed inset-0 z-40" onClick={() => setProfileDropdownOpen(false)} />
                  <div className="absolute right-0 mt-2 w-48 bg-surface-container-lowest rounded-xl shadow-lg border border-border-muted z-50 overflow-hidden flex flex-col py-1">
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

        {/* Page Content */}
        {children}
      </div>
    </div>
  );
}

export default function DashboardLayout({
  children,
}: {
  children: React.ReactNode;
}) {
  return (
    <ThemeProvider>
      <DashboardContent>{children}</DashboardContent>
    </ThemeProvider>
  );
}
