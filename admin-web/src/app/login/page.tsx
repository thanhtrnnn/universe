"use client";

import React, { useState, useEffect } from "react";
import { useRouter } from "next/navigation";
import Link from "next/link";
import { useAuthStore } from "@/store/authStore";

export default function LoginPage() {
  const router = useRouter();
  const { login, isLoading, error, isAuthenticated, clearError } = useAuthStore();
  const [form, setForm] = useState({ email: "", password: "" });
  const [showPassword, setShowPassword] = useState(false);

  // Redirect nếu đã đăng nhập
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
      // error đã được set vào store
    }
  };

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
          <p className="text-body-md text-on-surface-variant">Hệ sinh thái Đại học số — Admin</p>
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
              Email / Mã admin
            </label>
            <div className="relative flex items-center">
              <span className="material-symbols-outlined absolute left-2 text-outline select-none pointer-events-none">
                person
              </span>
              <input
                className="w-full pl-10 pr-3 py-2 bg-surface-container-lowest border border-outline-variant rounded-lg focus:outline-none focus:border-primary focus:ring-1 focus:ring-primary text-body-md text-on-surface placeholder:text-outline transition-all duration-200"
                id="email"
                name="email"
                placeholder="Nhập email hoặc mã của bạn"
                type="text"
                value={form.email}
                onChange={handleChange}
                required
                disabled={isLoading}
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
              <span className="material-symbols-outlined absolute left-2 text-outline select-none pointer-events-none">
                lock
              </span>
              <input
                className="w-full pl-10 pr-10 py-2 bg-surface-container-lowest border border-outline-variant rounded-lg focus:outline-none focus:border-primary focus:ring-1 focus:ring-primary text-body-md text-on-surface placeholder:text-outline transition-all duration-200"
                id="password"
                name="password"
                placeholder="••••••••"
                type={showPassword ? "text" : "password"}
                value={form.password}
                onChange={handleChange}
                required
                disabled={isLoading}
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

          {/* Submit Button */}
          <button
            className="w-full mt-2 bg-primary text-on-primary text-button py-2 px-lg rounded-lg shadow-sm hover:bg-on-primary-fixed-variant hover:shadow-md transition-all duration-200 active:scale-[0.98] flex justify-center items-center gap-1 disabled:opacity-60 disabled:cursor-not-allowed"
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
