"use client";
import React from "react";
import { useTheme } from "@/components/ThemeProvider";

export default function SettingsPage() {
  const { theme, toggleTheme } = useTheme();

  return (
    <main className="flex-1 overflow-y-auto mt-16 p-xl bg-background">
      <h2 className="text-h1 text-on-surface mb-1">Cài đặt</h2>
      <p className="text-body-md text-on-surface-variant mb-lg">Tùy chỉnh giao diện và quản lý tài khoản.</p>

      <div className="max-w-6xl">
        <div className="bg-surface-container-lowest rounded-xl p-xl shadow-[var(--shadow-card)] border border-border-muted mb-container_gutter">
          <h3 className="text-h3 text-on-surface mb-lg">Giao diện</h3>
          <div className="flex items-center justify-between py-md">
            <div>
              <p className="text-button text-on-surface">Chế độ tối (Dark Mode)</p>
              <p className="text-body-sm text-on-surface-variant mt-1">Chuyển đổi giữa giao diện sáng và tối</p>
            </div>
            <button onClick={toggleTheme} className={`relative w-14 h-8 rounded-full transition-colors duration-300 ${theme === "dark" ? "bg-primary" : "bg-surface-container-highest"}`}>
              <div className={`absolute top-1 w-6 h-6 rounded-full bg-white shadow-md transition-transform duration-300 flex items-center justify-center ${theme === "dark" ? "translate-x-7" : "translate-x-1"}`}>
                <span className="material-symbols-outlined text-[14px] text-on-surface-variant">{theme === "dark" ? "dark_mode" : "light_mode"}</span>
              </div>
            </button>
          </div>
        </div>

        <div className="bg-surface-container-lowest rounded-xl p-xl shadow-[var(--shadow-card)] border border-border-muted">
          <h3 className="text-h3 text-on-surface mb-lg">Tài khoản</h3>
          <div className="flex items-center justify-between py-md border-b border-border-muted">
            <div>
              <p className="text-button text-on-surface">Đổi mật khẩu</p>
              <p className="text-body-sm text-on-surface-variant mt-1">Cập nhật mật khẩu đăng nhập</p>
            </div>
            <button className="px-4 py-2 border border-outline-variant rounded-lg text-button text-on-surface hover:bg-surface-container transition-colors">Thay đổi</button>
          </div>
          <div className="flex items-center justify-between py-md">
            <div>
              <p className="text-button text-error">Đăng xuất tất cả thiết bị</p>
              <p className="text-body-sm text-on-surface-variant mt-1">Đăng xuất khỏi mọi phiên đăng nhập</p>
            </div>
            <button className="px-4 py-2 border border-error/30 rounded-lg text-button text-error hover:bg-error/10 transition-colors">Đăng xuất</button>
          </div>
        </div>
      </div>
    </main>
  );
}
