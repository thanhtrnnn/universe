"use client";

import React, { useState } from "react";

export default function LoginPage() {
  const [showPassword, setShowPassword] = useState(false);

  return (
    <div className="bg-background min-h-screen flex items-center justify-center p-lg font-inter text-on-surface antialiased">
      <main className="w-full max-w-[420px] bg-surface-container-lowest rounded-xl shadow-[var(--shadow-float)] border border-outline-variant/30 p-xl flex flex-col gap-xl">
        {/* Header */}
        <header className="flex flex-col items-center text-center gap-2">
          <div className="w-16 h-16 bg-primary-container rounded-full flex items-center justify-center mb-1">
            <span
              className="material-symbols-outlined text-on-primary-container text-[32px]"
              style={{ fontVariationSettings: "'FILL' 1" }}
            >
              school
            </span>
          </div>
          <h1 className="text-h1 text-primary">UniVerse</h1>
          <p className="text-body-md text-on-surface-variant">Hệ sinh thái Đại học số</p>
        </header>

        {/* Form */}
        <form action="#" className="flex flex-col gap-md" method="POST">
          {/* Email/Mã admin */}
          <div className="flex flex-col gap-1">
            <label className="text-label text-on-surface" htmlFor="admin-id">
              Email/Mã admin
            </label>
            <div className="relative flex items-center">
              <span className="material-symbols-outlined absolute left-2 text-outline select-none pointer-events-none">
                person
              </span>
              <input
                className="w-full pl-10 pr-3 py-2 bg-surface-container-lowest border border-outline-variant rounded-lg focus:outline-none focus:border-primary focus:ring-1 focus:ring-primary text-body-md text-on-surface placeholder:text-outline transition-all duration-200"
                id="admin-id"
                name="admin-id"
                placeholder="Nhập email hoặc mã của bạn"
                type="text"
              />
            </div>
          </div>

          {/* Mật khẩu */}
          <div className="flex flex-col gap-1">
            <label className="text-label text-on-surface" htmlFor="password">
              Mật khẩu
            </label>
            <div className="relative flex items-center">
              <span className="material-symbols-outlined absolute left-2 text-outline select-none pointer-events-none">
                lock
              </span>
              <input
                className="w-full pl-10 pr-10 py-2 bg-surface-container-lowest border border-outline-variant rounded-lg focus:outline-none focus:border-primary focus:ring-1 focus:ring-primary text-body-md text-on-surface placeholder:text-outline transition-all duration-200"
                id="password"
                name="password"
                placeholder="••••••••"
                type={showPassword ? "text" : "password"}
              />
              <button
                aria-label="Toggle password visibility"
                className="absolute right-2 text-outline hover:text-on-surface focus:outline-none transition-colors"
                type="button"
                onClick={() => setShowPassword(!showPassword)}
              >
                <span className="material-symbols-outlined">
                  {showPassword ? "visibility_off" : "visibility"}
                </span>
              </button>
            </div>
          </div>

          {/* Quên mật khẩu */}
          <div className="flex justify-end items-center mt-1">
            <a
              className="text-body-sm text-primary hover:text-on-primary-fixed-variant transition-colors hover:underline"
              href="#"
            >
              Quên mật khẩu?
            </a>
          </div>

          {/* Button */}
          <button
            className="w-full mt-2 bg-primary text-on-primary text-button py-2 px-lg rounded-lg shadow-sm hover:bg-on-primary-fixed-variant hover:shadow-md transition-all duration-200 active:scale-[0.98] flex justify-center items-center gap-1"
            type="submit"
          >
            <span>Đăng nhập</span>
            <span className="material-symbols-outlined text-[18px]">login</span>
          </button>
        </form>

        {/* Footer */}
        <footer className="text-center mt-2">
          <p className="text-body-sm text-outline">
            Bảo mật thông tin hệ thống là ưu tiên hàng đầu.
          </p>
        </footer>
      </main>
    </div>
  );
}
