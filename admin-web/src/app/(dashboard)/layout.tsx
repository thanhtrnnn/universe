"use client";

import React from "react";
import Link from "next/link";
import { usePathname } from "next/navigation";
import { ThemeProvider, useTheme } from "@/components/ThemeProvider";

const navItems = [
  { href: "/", label: "Bảng điều khiển", icon: "dashboard" },
  { href: "/users", label: "Người dùng", icon: "group" },
  { href: "/classes", label: "Lớp học", icon: "school" },
  { href: "/statistics", label: "Thống kê", icon: "analytics" },
  { href: "/notifications", label: "Thông báo", icon: "campaign" },
  { href: "/settings", label: "Cài đặt", icon: "settings" },
];

const pageTitles: Record<string, string> = {
  "/": "Tổng quan hệ thống",
  "/users": "Quản lý người dùng",
  "/users/new": "Thêm Người dùng mới",
  "/classes": "Quản lý Lớp học",
  "/statistics": "Thống kê & Báo cáo",
  "/notifications": "Gửi thông báo hệ thống",
  "/settings": "Cài đặt",
};

function DashboardContent({ children }: { children: React.ReactNode }) {
  const pathname = usePathname();
  const { theme, toggleTheme } = useTheme();

  const getPageTitle = () => {
    if (pageTitles[pathname]) return pageTitles[pathname];
    if (pathname.startsWith("/classes/")) return "Chi tiết Lớp học";
    if (pathname.startsWith("/users/")) return "Thêm Người dùng mới";
    return "UniVerse Admin";
  };

  return (
    <div className="bg-background text-on-surface font-inter min-h-screen flex">
      {/* ===== SIDEBAR ===== */}
      <nav className="w-[240px] h-screen fixed left-0 top-0 border-r border-zinc-800 bg-sidebar-bg shadow-2xl flex flex-col py-6 z-50">
        {/* Brand */}
        <div className="px-6 mb-8 flex items-center gap-3">
          <div className="w-8 h-8 rounded-full bg-primary-container flex items-center justify-center">
            <span className="material-symbols-outlined text-white text-[18px]">school</span>
          </div>
          <div>
            <h1 className="text-h2 text-white tracking-tight" style={{ fontSize: '18px', lineHeight: '24px' }}>UniVerse</h1>
            <p className="text-body-sm text-zinc-400">Hệ thống quản trị</p>
          </div>
        </div>

        {/* Navigation Links */}
        <div className="flex-1 flex flex-col gap-[4px] px-[8px]">
          {navItems.map((item) => {
            const isActive =
              pathname === item.href ||
              (item.href !== "/" && pathname.startsWith(item.href));
            return (
              <Link
                key={item.href}
                href={item.href}
                className={`flex items-center gap-3 px-3 py-2 rounded-md transition-colors duration-200 ${
                  isActive
                    ? "bg-[#6C63FF]/10 text-[#6C63FF] border-l-4 border-[#6C63FF] font-semibold"
                    : "text-zinc-400 hover:text-white hover:bg-zinc-800"
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

        {/* Footer */}
        <div className="mt-auto px-[8px]">
          <Link
            href="/login"
            className="flex items-center gap-3 px-3 py-2 rounded-md text-zinc-400 hover:text-white hover:bg-zinc-800 transition-colors duration-200"
          >
            <span className="material-symbols-outlined">logout</span>
            <span className="text-button">Đăng xuất</span>
          </Link>
        </div>
      </nav>

      {/* ===== MAIN CONTENT AREA ===== */}
      <div className="flex-1 ml-[240px] flex flex-col min-h-screen">
        {/* ===== TOP NAV BAR ===== */}
        <header className="fixed top-0 right-0 left-[240px] h-16 z-40 bg-surface-container-lowest/80 backdrop-blur-md border-b border-border-muted shadow-sm flex justify-between items-center px-8">
          {/* Left: Page Title */}
          <div className="flex items-center">
            <h2 className="text-h3 text-on-surface">{getPageTitle()}</h2>
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

            <button className="text-on-surface-variant hover:bg-surface-container rounded-full p-2 transition-colors focus:ring-2 focus:ring-primary/20">
              <span className="material-symbols-outlined">notifications</span>
            </button>
            <button className="text-on-surface-variant hover:bg-surface-container rounded-full p-2 transition-colors focus:ring-2 focus:ring-primary/20">
              <span className="material-symbols-outlined">help_outline</span>
            </button>

            <div className="w-px h-6 bg-border-muted mx-1"></div>

            <button className="flex items-center gap-2 hover:bg-surface-container rounded-full pr-3 transition-colors focus:ring-2 focus:ring-primary/20">
              <div className="w-8 h-8 rounded-full border border-border-muted bg-primary-container flex items-center justify-center overflow-hidden">
                <span className="material-symbols-outlined text-white text-[16px]">person</span>
              </div>
              <span className="text-button text-on-surface-variant hidden md:block">Admin</span>
            </button>
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
