"use client";

import React from "react";
import Link from "next/link";

export default function NewUserPage() {
  return (
    <main className="flex-1 overflow-y-auto mt-16 p-xl bg-background">
      <div className="max-w-4xl mx-auto">
        {/* Page Header */}
        <div className="mb-lg flex items-center justify-between">
          <div>
            <h2 className="text-h1 text-on-surface">Thêm Người dùng mới</h2>
            <p className="text-body-sm text-on-surface-variant mt-1">
              Nhập thông tin chi tiết để tạo tài khoản người dùng mới trong hệ thống.
            </p>
          </div>
          <Link
            href="/users"
            className="flex items-center gap-2 text-on-surface-variant hover:text-primary transition-colors text-button"
          >
            <span className="material-symbols-outlined text-lg">arrow_back</span>
            Quay lại
          </Link>
        </div>

        {/* Form Card */}
        <div className="bg-surface-container-lowest rounded-xl shadow-[var(--shadow-card)] border border-border-muted overflow-hidden">
          <form action="#" className="p-lg">
            <div className="flex flex-col md:flex-row gap-lg">
              {/* Left: Avatar Upload */}
              <div className="w-full md:w-1/3 flex flex-col items-center">
                <label className="text-label text-on-surface-variant mb-4 self-start md:self-center">
                  Ảnh đại diện
                </label>
                <div className="relative group cursor-pointer w-40 h-40 rounded-full border-2 border-dashed border-outline-variant bg-surface-container flex flex-col items-center justify-center overflow-hidden hover:border-primary hover:bg-surface-container-high transition-all">
                  <span className="material-symbols-outlined text-4xl text-outline mb-2 group-hover:text-primary">
                    add_a_photo
                  </span>
                  <span className="text-body-sm text-outline group-hover:text-primary text-center px-4">
                    Tải ảnh lên<br />(Max 2MB)
                  </span>
                  <input accept="image/*" className="hidden" type="file" />
                </div>
              </div>

              {/* Right: Input Fields */}
              <div className="w-full md:w-2/3 space-y-md">
                <div className="grid grid-cols-1 md:grid-cols-2 gap-md">
                  <div>
                    <label className="block text-label text-on-surface-variant mb-1" htmlFor="user_id">
                      Mã định danh <span className="text-error">*</span>
                    </label>
                    <input
                      className="w-full bg-surface-container-lowest border border-outline-variant rounded-lg px-4 py-2 text-body-md text-on-surface focus:outline-none focus:border-primary focus:ring-1 focus:ring-primary transition-colors"
                      id="user_id"
                      placeholder="VD: SV2023001"
                      required
                      type="text"
                    />
                  </div>
                  <div>
                    <label className="block text-label text-on-surface-variant mb-1" htmlFor="full_name">
                      Họ và tên <span className="text-error">*</span>
                    </label>
                    <input
                      className="w-full bg-surface-container-lowest border border-outline-variant rounded-lg px-4 py-2 text-body-md text-on-surface focus:outline-none focus:border-primary focus:ring-1 focus:ring-primary transition-colors"
                      id="full_name"
                      placeholder="Nhập họ và tên"
                      required
                      type="text"
                    />
                  </div>
                </div>

                <div className="grid grid-cols-1 md:grid-cols-2 gap-md">
                  <div>
                    <label className="block text-label text-on-surface-variant mb-1" htmlFor="email">
                      Email <span className="text-error">*</span>
                    </label>
                    <input
                      className="w-full bg-surface-container-lowest border border-outline-variant rounded-lg px-4 py-2 text-body-md text-on-surface focus:outline-none focus:border-primary focus:ring-1 focus:ring-primary transition-colors"
                      id="email"
                      placeholder="example@universe.edu.vn"
                      required
                      type="email"
                    />
                  </div>
                  <div>
                    <label className="block text-label text-on-surface-variant mb-1" htmlFor="phone">
                      Số điện thoại
                    </label>
                    <input
                      className="w-full bg-surface-container-lowest border border-outline-variant rounded-lg px-4 py-2 text-body-md text-on-surface focus:outline-none focus:border-primary focus:ring-1 focus:ring-primary transition-colors"
                      id="phone"
                      placeholder="Nhập số điện thoại"
                      type="tel"
                    />
                  </div>
                </div>

                <div className="grid grid-cols-1 md:grid-cols-2 gap-md">
                  <div>
                    <label className="block text-label text-on-surface-variant mb-1" htmlFor="role">
                      Chọn Vai trò <span className="text-error">*</span>
                    </label>
                    <div className="relative">
                      <select
                        className="w-full bg-surface-container-lowest border border-outline-variant rounded-lg px-4 py-2 text-body-md text-on-surface appearance-none focus:outline-none focus:border-primary focus:ring-1 focus:ring-primary transition-colors pr-10"
                        id="role"
                        required
                      >
                        <option disabled selected value="">-- Chọn vai trò --</option>
                        <option value="student">Sinh viên</option>
                        <option value="teacher">Giảng viên</option>
                        <option value="admin">Quản trị viên</option>
                      </select>
                      <span className="material-symbols-outlined absolute right-3 top-1/2 -translate-y-1/2 text-outline-variant pointer-events-none">
                        expand_more
                      </span>
                    </div>
                  </div>
                  <div>
                    <label className="block text-label text-on-surface-variant mb-1" htmlFor="department">
                      Chọn Khoa
                    </label>
                    <div className="relative">
                      <select
                        className="w-full bg-surface-container-lowest border border-outline-variant rounded-lg px-4 py-2 text-body-md text-on-surface appearance-none focus:outline-none focus:border-primary focus:ring-1 focus:ring-primary transition-colors pr-10"
                        id="department"
                      >
                        <option disabled selected value="">-- Chọn khoa --</option>
                        <option value="it">Công nghệ Thông tin</option>
                        <option value="business">Quản trị Kinh doanh</option>
                        <option value="design">Thiết kế Đồ họa</option>
                        <option value="languages">Ngoại ngữ</option>
                      </select>
                      <span className="material-symbols-outlined absolute right-3 top-1/2 -translate-y-1/2 text-outline-variant pointer-events-none">
                        expand_more
                      </span>
                    </div>
                  </div>
                </div>
              </div>
            </div>

            {/* Actions */}
            <div className="mt-xl pt-lg border-t border-surface-container-highest flex justify-end gap-2">
              <Link
                href="/users"
                className="px-6 py-2 rounded-lg bg-surface-container hover:bg-surface-container-high text-on-surface text-button transition-colors h-10 flex items-center"
              >
                Huỷ
              </Link>
              <button
                className="px-6 py-2 rounded-lg bg-primary hover:bg-on-primary-fixed-variant text-on-primary text-button transition-colors shadow-sm h-10 flex items-center gap-2"
                type="submit"
              >
                <span className="material-symbols-outlined text-sm">save</span>
                Lưu
              </button>
            </div>
          </form>
        </div>
      </div>
    </main>
  );
}
