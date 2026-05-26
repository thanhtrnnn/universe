"use client";

import React, { useState, useEffect, useRef } from "react";
import Link from "next/link";
import { usePathname } from "next/navigation";
import { ThemeProvider, useTheme } from "@/components/ThemeProvider";
import { useAuthStore } from "@/store/authStore";

const navSections = [
  {
    title: "DASHBOARD & HỌC TẬP",
    items: [
      { href: "/", label: "Trang chủ", icon: "home" },
      { href: "/notifications", label: "Thông báo", icon: "campaign" },
      { href: "/weekly-schedule", label: "Lịch học tuần", icon: "view_week" },
      { href: "/semester-schedule", label: "Lịch học kỳ", icon: "calendar_month" },
      { href: "/exam-schedule", label: "Lịch thi", icon: "event_note" },
      { href: "/attendance", label: "Điểm danh thông minh", icon: "how_to_reg" },
      { href: "/course-registration", label: "Đăng ký tín chỉ", icon: "app_registration" },
      { href: "/progress", label: "Tiến độ học tập", icon: "donut_large" },
      { href: "/grades", label: "Kết quả học tập", icon: "grade" },
    ]
  },
  {
    title: "KHÁC",
    items: [
      { href: "/messages", label: "Tin nhắn Chat", icon: "forum" },
    ]
  }
];

const pageTitles: Record<string, string> = {
  "/": "Trang chủ",
  "/notifications": "Thông báo từ ban quản trị",
  "/course-registration": "Đăng ký tín chỉ",
  "/progress": "Theo dõi tiến độ học tập",
  "/weekly-schedule": "Thời khóa biểu tuần",
  "/semester-schedule": "Thời khóa biểu học kỳ",
  "/exam-schedule": "Lịch thi",
  "/grades": "Kết quả học tập",
  "/attendance": "Điểm danh thông minh",
  "/messages": "Tin nhắn Chat",
};

function DashboardContent({ children }: { children: React.ReactNode }) {
  const pathname = usePathname();
  const { theme, toggleTheme } = useTheme();
  const [sidebarOpen, setSidebarOpen] = useState(false);
  const [showUserMenu, setShowUserMenu] = useState(false);
  const userMenuRef = useRef<HTMLDivElement>(null);
  const { user, logout } = useAuthStore();

  useEffect(() => {
    const handleClickOutside = (event: MouseEvent) => {
      if (userMenuRef.current && !userMenuRef.current.contains(event.target as Node)) {
        setShowUserMenu(false);
      }
    };
    document.addEventListener("mousedown", handleClickOutside);
    return () => document.removeEventListener("mousedown", handleClickOutside);
  }, []);

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
      <nav className={`w-[260px] h-screen fixed left-0 top-0 border-r border-outline-variant bg-surface-container-lowest flex flex-col py-6 z-50 transition-transform duration-300 md:translate-x-0 ${sidebarOpen ? 'translate-x-0' : '-translate-x-full'}`}>
        <div className="px-6 mb-4 flex items-center gap-3">
          <div className="w-8 h-8 bg-primary rounded-lg flex items-center justify-center text-on-primary">
            <span className="material-symbols-outlined text-[20px]">school</span>
          </div>
          <div>
            <h1 className="text-primary font-black text-xl leading-tight">UniVerse</h1>
            <p className="text-[11px] text-on-surface-variant uppercase tracking-wider font-semibold">Student Portal</p>
          </div>
        </div>

        <div className="flex-1 flex flex-col gap-4 px-3 overflow-y-auto no-scrollbar pb-6 mt-4">
          {navSections.map((section, idx) => (
            <div key={idx} className="flex flex-col gap-1">
              <h3 className="px-3 text-[11px] font-bold text-outline uppercase tracking-wider mb-1">{section.title}</h3>
              {section.items.map((item) => {
                const isActive = pathname === item.href || (item.href !== "/" && pathname.startsWith(item.href));
                return (
                  <Link key={item.href} href={item.href}
                    className={`flex items-center gap-3 px-3 py-2 rounded-lg transition-all duration-200 ${
                      isActive ? "bg-primary/10 text-primary font-semibold" : "text-on-surface-variant hover:text-on-surface hover:bg-surface-variant/50"
                    }`}>
                    <span className="material-symbols-outlined text-[20px]" style={isActive ? { fontVariationSettings: "'FILL' 1" } : {}}>{item.icon}</span>
                    <span className="text-body-sm font-medium">{item.label}</span>
                  </Link>
                );
              })}
            </div>
          ))}
        </div>

        <div className="mt-auto px-4 pt-4 border-t border-outline-variant/30">
          <button onClick={logout} className="w-full flex items-center gap-3 px-3 py-2 rounded-lg text-on-surface-variant hover:text-error hover:bg-error-container/50 transition-colors duration-200">
            <span className="material-symbols-outlined text-[20px]">logout</span>
            <span className="text-body-sm font-medium">Đăng xuất</span>
          </button>
        </div>
      </nav>

      {/* Main */}
      <div className="flex-1 md:ml-[260px] flex flex-col min-h-screen w-full">
        <header className="fixed top-0 right-0 left-0 md:left-[260px] h-16 z-30 bg-surface-container-lowest/80 backdrop-blur-md border-b border-border-muted shadow-sm flex justify-between items-center px-4 md:px-8">
          <div className="flex items-center">
            <button
              className="md:hidden text-on-surface-variant hover:bg-surface-container p-2 rounded-full mr-2 flex items-center justify-center transition-colors"
              onClick={() => setSidebarOpen(true)}
            >
              <span className="material-symbols-outlined">menu</span>
            </button>
            <h2 className="text-h3 text-on-surface hidden sm:block">{pageTitles[pathname] || "UniVerse Student"}</h2>
          </div>
          <div className="flex items-center gap-2">
            <button onClick={toggleTheme} className="text-on-surface-variant hover:bg-surface-container rounded-full p-2 transition-colors" title={theme === "light" ? "Dark Mode" : "Light Mode"}>
              <span className="material-symbols-outlined">{theme === "light" ? "dark_mode" : "light_mode"}</span>
            </button>
            <Link href="/notifications" className="text-on-surface-variant hover:bg-surface-container rounded-full p-2 transition-colors">
              <span className="material-symbols-outlined">notifications</span>
            </Link>
            <div className="w-px h-6 bg-border-muted mx-2"></div>
            <div className="relative" ref={userMenuRef}>
              <button 
                onClick={() => setShowUserMenu(!showUserMenu)}
                className="flex items-center gap-3 hover:bg-surface-container rounded-full pr-4 py-1 pl-1 transition-colors border border-transparent hover:border-border-muted"
              >
                <img src="https://i.pravatar.cc/150?img=12" alt="Avatar" className="w-9 h-9 rounded-full object-cover border border-outline-variant" />
                <div className="flex flex-col items-start hidden md:flex">
                  <span className="text-button text-on-surface leading-tight">{user?.name || "Sinh viên"}</span>
                  <span className="text-[11px] text-on-surface-variant leading-tight">{user?.code || "Mã SV"}</span>
                </div>
              </button>
              
              {showUserMenu && (
                <div className="absolute right-0 top-full mt-2 w-56 bg-surface-container-lowest border border-border-muted rounded-xl shadow-[var(--shadow-float)] overflow-hidden z-50">
                  <div className="p-4 border-b border-border-muted bg-surface">
                    <p className="text-body-md font-semibold text-on-surface">{user?.name || "Phạm Thị Thiên Hà"}</p>
                    <p className="text-body-sm text-on-surface-variant">{user?.code || "B23DCCN266"}</p>
                  </div>
                  <div className="py-2">
                    <Link href="/profile" onClick={() => setShowUserMenu(false)} className="w-full px-4 py-2 text-left text-body-sm text-on-surface hover:bg-surface-container transition-colors flex items-center gap-3">
                      <span className="material-symbols-outlined text-[18px]">person</span>
                      Hồ sơ cá nhân
                    </Link>
                    <button className="w-full px-4 py-2 text-left text-body-sm text-on-surface hover:bg-surface-container transition-colors flex items-center gap-3">
                      <span className="material-symbols-outlined text-[18px]">settings</span>
                      Cài đặt tài khoản
                    </button>
                    <button className="w-full px-4 py-2 text-left text-body-sm text-on-surface hover:bg-surface-container transition-colors flex items-center gap-3">
                      <span className="material-symbols-outlined text-[18px]">lock_reset</span>
                      Đổi mật khẩu
                    </button>
                    <div className="h-px bg-border-muted my-1 w-full" />
                    <button onClick={logout} className="w-full px-4 py-2 text-left text-body-sm text-error hover:bg-error-container/20 transition-colors flex items-center gap-3">
                      <span className="material-symbols-outlined text-[18px]">logout</span>
                      Đăng xuất
                    </button>
                  </div>
                </div>
              )}
            </div>
          </div>
        </header>
        <div className="pt-16 p-4 md:p-8 max-w-[1200px] mx-auto w-full">{children}</div>
      </div>
    </div>
  );
}

export default function DashboardLayout({ children }: { children: React.ReactNode }) {
  return <ThemeProvider><DashboardContent>{children}</DashboardContent></ThemeProvider>;
}
