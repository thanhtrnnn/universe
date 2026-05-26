"use client";

import React, { useState, useEffect } from "react";
import { useRouter } from "next/navigation";
import Link from "next/link";
import { useAuthStore } from "@/store/authStore";

export default function LecturerLoginPage() {
  const router = useRouter();
  const { login, isLoading, error, isAuthenticated, clearError } = useAuthStore();
  const [form, setForm] = useState({ email: "", password: "" });
  const [showPassword, setShowPassword] = useState(false);

  useEffect(() => {
    if (isAuthenticated) router.replace("/");
  }, [isAuthenticated, router]);

  const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    clearError();
    setForm((prev) => ({ ...prev, [e.target.name]: e.target.value }));
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    try {
      await login(form.email, form.password);
      router.replace("/");
    } catch {
      // error đã set trong store
    }
  };

  return (
    <div className="bg-background min-h-screen flex items-center justify-center p-lg font-inter text-on-surface antialiased">
      <main className="w-full max-w-[440px] bg-surface-container-lowest rounded-2xl shadow-[var(--shadow-float)] border border-outline-variant/30 p-xl flex flex-col gap-xl">
        {/* Header */}
        <header className="flex flex-col items-center text-center gap-3">
          <div className="w-16 h-16 bg-tertiary-container rounded-2xl flex items-center justify-center mb-1 shadow-md">
            <span
              className="material-symbols-outlined text-on-tertiary-container text-[32px]"
              style={{ fontVariationSettings: "'FILL' 1" }}
            >
              cast_for_education
            </span>
          </div>
          <h1 className="text-h1 text-primary">UniVerse</h1>
          <div className="flex flex-col gap-1">
            <p className="text-body-md font-semibold text-on-surface">Cổng thông tin Giảng viên</p>
            <p className="text-body-sm text-on-surface-variant">Đăng nhập để quản lý lớp học của bạn</p>
          </div>
        </header>

        {/* Error Alert */}
        {error && (
          <div className="flex items-center gap-2 bg-error-container text-on-error-container px-md py-sm rounded-lg text-body-sm" role="alert">
            <span className="material-symbols-outlined text-[18px]" style={{ fontVariationSettings: "'FILL' 1" }}>error</span>
            <span>{error}</span>
          </div>
        )}

        {/* Form */}
        <form className="flex flex-col gap-md" onSubmit={handleSubmit}>
          {/* Email */}
          <div className="flex flex-col gap-1">
            <label className="text-label text-on-surface" htmlFor="email">
              Email giảng viên
            </label>
            <div className="relative flex items-center">
              <span className="material-symbols-outlined absolute left-3 text-outline select-none pointer-events-none text-[20px]">
                person
              </span>
              <input
                className="w-full pl-10 pr-4 py-2.5 bg-surface-container-lowest border border-outline-variant rounded-lg focus:outline-none focus:border-primary focus:ring-1 focus:ring-primary text-body-md text-on-surface placeholder:text-outline transition-all duration-200"
                id="email"
                name="email"
                placeholder="gv@university.edu.vn"
                type="email"
                value={form.email}
                onChange={handleChange}
                required
                disabled={isLoading}
                autoComplete="email"
              />
            </div>
          </div>

          {/* Password */}
          <div className="flex flex-col gap-[4px]">
            <div className="flex justify-between items-center">
              <label className="text-label font-label text-on-surface" htmlFor="password">
                Mật khẩu
              </label>
              <Link
                href="/forgot-password"
                className="text-body-sm text-primary hover:text-primary-container transition-colors font-medium"
              >
                Quên mật khẩu?
              </Link>
            </div>
            <div className="relative flex items-center">
              <span className="material-symbols-outlined absolute left-3 text-outline select-none pointer-events-none text-[20px]">
                lock
              </span>
              <input
                className="w-full pl-10 pr-10 py-2.5 bg-surface-container-lowest border border-outline-variant rounded-lg focus:outline-none focus:border-primary focus:ring-1 focus:ring-primary text-body-md text-on-surface placeholder:text-outline transition-all duration-200"
                id="password"
                name="password"
                placeholder="••••••••"
                type={showPassword ? "text" : "password"}
                value={form.password}
                onChange={handleChange}
                required
                disabled={isLoading}
                autoComplete="current-password"
              />
              <button
                aria-label="Toggle password visibility"
                className="absolute right-3 text-outline hover:text-on-surface focus:outline-none transition-colors"
                type="button"
                onClick={() => setShowPassword(!showPassword)}
              >
                <span className="material-symbols-outlined text-[20px]">
                  {showPassword ? "visibility_off" : "visibility"}
                </span>
              </button>
            </div>
          </div>

          <div className="flex justify-end">
            <a className="text-body-sm text-primary hover:underline transition-colors" href="#">
              Quên mật khẩu?
            </a>
          </div>

          {/* Submit */}
          <button
            className="w-full mt-1 bg-primary text-on-primary text-button py-2.5 px-lg rounded-lg shadow-sm hover:bg-on-primary-fixed-variant hover:shadow-md transition-all duration-200 active:scale-[0.98] flex justify-center items-center gap-2 disabled:opacity-60 disabled:cursor-not-allowed"
            type="submit"
            disabled={isLoading}
          >
            {isLoading ? (
              <>
                <span className="material-symbols-outlined text-[18px] animate-spin">progress_activity</span>
                <span>Đang đăng nhập...</span>
              </>
            ) : (
              <>
                <span>Đăng nhập</span>
                <span className="material-symbols-outlined text-[18px]">login</span>
              </>
            )}
          </button>
        </form>

        <footer className="text-center">
          <p className="text-body-sm text-outline">
            Hệ thống dành riêng cho Giảng viên • UniVerse &copy; 2024
          </p>
        </footer>
      </main>
    </div>
  );
}
