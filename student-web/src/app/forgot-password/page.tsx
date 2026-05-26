"use client";

import React, { useState } from "react";
import Link from "next/link";
import api from "@/lib/api";

export default function ForgotPasswordPage() {
  const [email, setEmail] = useState("");
  const [isLoading, setIsLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [success, setSuccess] = useState(false);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!email) return;

    setIsLoading(true);
    setError(null);
    try {
      // Mock API call
      await new Promise(resolve => setTimeout(resolve, 1000));
      // await api.post("/auth/forgot-password", { email });
      setSuccess(true);
    } catch (err) {
      setError("Không thể gửi email. Vui lòng kiểm tra lại địa chỉ email.");
    } finally {
      setIsLoading(false);
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
              lock_reset
            </span>
          </div>
          <h1 className="text-h2 font-semibold text-primary">Quên mật khẩu</h1>
          <p className="text-body-md text-on-surface-variant">
            Nhập email của bạn, chúng tôi sẽ gửi liên kết để đặt lại mật khẩu.
          </p>
        </header>

        {/* Alerts */}
        {error && (
          <div className="flex items-center gap-2 bg-error-container text-on-error-container px-md py-sm rounded-lg text-body-sm" role="alert">
            <span className="material-symbols-outlined text-[18px]" style={{ fontVariationSettings: "'FILL' 1" }}>error</span>
            <span>{error}</span>
          </div>
        )}

        {success ? (
          <div className="flex flex-col items-center gap-4 text-center">
            <div className="bg-secondary-container text-on-secondary-container p-4 rounded-xl mb-2 flex flex-col items-center gap-3 w-full">
               <span className="material-symbols-outlined text-[32px]" style={{ fontVariationSettings: "'FILL' 1" }}>mark_email_read</span>
               <p className="font-body-md">Liên kết đặt lại mật khẩu đã được gửi đến <strong>{email}</strong>.</p>
            </div>
            <Link
              href="/login"
              className="w-full py-sm bg-primary text-on-primary rounded-lg font-button text-button hover:opacity-90 transition-colors shadow flex items-center justify-center"
            >
              Quay lại đăng nhập
            </Link>
          </div>
        ) : (
          <form className="flex flex-col gap-md" onSubmit={handleSubmit}>
            {/* Email Input */}
            <div className="flex flex-col gap-[4px]">
              <label htmlFor="email" className="text-label font-label text-on-surface">
                Email
              </label>
              <div className="relative">
                <span className="absolute left-3 top-1/2 -translate-y-1/2 material-symbols-outlined text-outline text-[20px]">
                  mail
                </span>
                <input
                  id="email"
                  name="email"
                  type="email"
                  placeholder="giangvien@universe.edu.vn"
                  className="w-full pl-10 pr-4 py-sm bg-surface rounded-lg border border-outline-variant focus:border-primary focus:ring-1 focus:ring-primary outline-none transition-colors font-body-md text-on-surface placeholder:text-outline disabled:opacity-50"
                  value={email}
                  onChange={(e) => { setError(null); setEmail(e.target.value); }}
                  disabled={isLoading}
                  required
                />
              </div>
            </div>

            {/* Actions */}
            <div className="flex flex-col gap-3 mt-sm">
              <button
                type="submit"
                disabled={isLoading || !email}
                className="w-full py-sm bg-primary text-on-primary rounded-lg font-button text-button hover:opacity-90 transition-colors disabled:opacity-50 shadow flex justify-center items-center h-11"
              >
                {isLoading ? (
                  <span className="material-symbols-outlined text-[20px] animate-spin">progress_activity</span>
                ) : (
                  "Gửi liên kết khôi phục"
                )}
              </button>
              <Link
                href="/login"
                className="w-full py-sm text-primary rounded-lg font-button text-button hover:bg-surface-container-low transition-colors flex justify-center items-center h-11"
              >
                Quay lại đăng nhập
              </Link>
            </div>
          </form>
        )}
      </main>
    </div>
  );
}
