"use client";

import React from "react";
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
  { href: "/messages", label: "Trung tâm tin nhắn", icon: "campaign" },
  { href: "/settings", label: "Cài đặt", icon: "settings" },
];

const pageTitles: Record<string, string> = {
  "/": "Tổng quan giảng viên",
  "/classes": "Quản lý lớp học",
  "/attendance": "Điểm danh sinh viên",
  "/grades": "Nhập & Quản lý Điểm số",
  "/statistics": "Thống kê Chuyên cần",
  "/leave-requests": "Duyệt đơn Vắng phép",
  "/messages": "Trung tâm tin nhắn",
  "/settings": "Cài đặt",
};

function DashboardContent({ children }: { children: React.ReactNode }) {
  const pathname = usePathname();
  const { theme, toggleTheme } = useTheme();

  return (
    <div className="bg-background text-on-surface font-inter min-h-screen flex text-body-md antialiased">
      {/* Sidebar */}
      <nav className="w-[240px] h-screen fixed left-0 top-0 border-r border-outline-variant bg-surface-container flex flex-col py-6 z-50">
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
      <div className="flex-1 ml-[240px] flex flex-col min-h-screen">
        <header className="fixed top-0 right-0 left-[240px] h-16 z-40 bg-surface-container-lowest/80 backdrop-blur-md border-b border-border-muted shadow-sm flex justify-between items-center px-8">
          <h2 className="text-h3 text-on-surface">{pageTitles[pathname] || "UniVerse Lecturer"}</h2>
          <div className="flex items-center gap-3">
            <button onClick={toggleTheme} className="text-on-surface-variant hover:bg-surface-container rounded-full p-2 transition-colors" title={theme === "light" ? "Dark Mode" : "Light Mode"}>
              <span className="material-symbols-outlined">{theme === "light" ? "dark_mode" : "light_mode"}</span>
            </button>
            <button className="text-on-surface-variant hover:bg-surface-container rounded-full p-2 transition-colors">
              <span className="material-symbols-outlined">notifications</span>
            </button>
            <div className="w-px h-6 bg-border-muted mx-1"></div>
            <button className="flex items-center gap-2 hover:bg-surface-container rounded-full pr-3 transition-colors">
              <div className="w-8 h-8 rounded-full border border-border-muted bg-tertiary-container flex items-center justify-center overflow-hidden">
                <span className="material-symbols-outlined text-white text-[16px]">person</span>
              </div>
              <span className="text-button text-on-surface-variant hidden md:block">Giảng viên</span>
            </button>
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
