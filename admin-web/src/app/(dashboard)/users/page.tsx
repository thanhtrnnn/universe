"use client";

import React, { useState, useEffect, useCallback, useRef } from "react";
import Link from "next/link";
import api from "@/lib/api";
import type { User, PaginatedResponse, UserRole } from "@/types";

// ── Skeleton ──────────────────────────────────────────────────
function SkeletonRow() {
  return (
    <tr className="border-b border-border-muted/50 animate-pulse">
      {[...Array(7)].map((_, i) => (
        <td key={i} className="py-3 px-4">
          <div className="h-4 bg-surface-container-high rounded w-3/4" />
        </td>
      ))}
    </tr>
  );
}

// ── Role Badge ────────────────────────────────────────────────
const roleLabelMap: Record<UserRole, string> = {
  student: "Sinh viên",
  lecturer: "Giảng viên",
  admin: "Quản trị viên",
};

const statusStyleMap: Record<string, string> = {
  active: "bg-secondary-container text-on-secondary-container",
  suspended: "bg-error-container text-on-error-container",
  graduated: "bg-surface-container-high text-on-surface-variant",
};

const statusLabelMap: Record<string, string> = {
  active: "Đang hoạt động",
  suspended: "Đình chỉ",
  graduated: "Tốt nghiệp",
};

// ── Delete Confirm Modal ──────────────────────────────────────
function DeleteModal({ user, onConfirm, onCancel, isDeleting }: {
  user: User; onConfirm: () => void; onCancel: () => void; isDeleting: boolean;
}) {
  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/40 backdrop-blur-sm">
      <div className="bg-surface-container-lowest rounded-xl shadow-[var(--shadow-float)] border border-border-muted p-xl w-full max-w-md mx-4">
        <div className="flex items-center gap-3 mb-md">
          <div className="w-10 h-10 rounded-full bg-error-container flex items-center justify-center">
            <span className="material-symbols-outlined text-error text-[20px]" style={{ fontVariationSettings: "'FILL' 1" }}>delete</span>
          </div>
          <h3 className="text-h3 text-on-surface">Xác nhận xóa</h3>
        </div>
        <p className="text-body-md text-on-surface-variant mb-lg">
          Bạn có chắc muốn xóa tài khoản <strong className="text-on-surface">{user.name}</strong> ({user.code})?
          Hành động này không thể hoàn tác.
        </p>
        <div className="flex gap-3 justify-end">
          <button onClick={onCancel} disabled={isDeleting} className="px-4 py-2 rounded-lg border border-outline-variant text-on-surface hover:bg-surface-container text-button transition-colors">Hủy</button>
          <button onClick={onConfirm} disabled={isDeleting} className="px-4 py-2 rounded-lg bg-error text-on-error text-button hover:bg-error/90 transition-colors flex items-center gap-2 disabled:opacity-60">
            {isDeleting && <span className="material-symbols-outlined text-[16px] animate-spin">progress_activity</span>}
            Xóa tài khoản
          </button>
        </div>
      </div>
    </div>
  );
}

// ── Bulk Import Modal ───────────────────────────────────────────
function ImportModal({ onClose, onSuccess }: { onClose: () => void; onSuccess: () => void }) {
  const [file, setFile] = useState<File | null>(null);
  const [isUploading, setIsUploading] = useState(false);

  const handleDrop = (e: React.DragEvent) => {
    e.preventDefault();
    if (e.dataTransfer.files && e.dataTransfer.files[0]) {
      setFile(e.dataTransfer.files[0]);
    }
  };

  const handleUpload = async () => {
    if (!file) return;
    setIsUploading(true);
    try {
      // Mock API call for upload
      await new Promise((res) => setTimeout(res, 1500));
      onSuccess();
    } catch {
      alert("Lỗi khi tải lên danh sách.");
    } finally {
      setIsUploading(false);
    }
  };

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/40 backdrop-blur-sm p-4">
      <div className="bg-surface-container-lowest rounded-xl shadow-[var(--shadow-float)] border border-border-muted p-xl w-full max-w-3xl">
        <div className="flex justify-between items-center mb-lg">
          <h3 className="text-h3 text-on-surface">Nhập danh sách từ Excel/CSV</h3>
          <button onClick={onClose} className="text-on-surface-variant hover:bg-surface-container rounded-full p-1 transition-colors">
            <span className="material-symbols-outlined">close</span>
          </button>
        </div>
        
        <div 
          onDragOver={(e) => e.preventDefault()} 
          onDrop={handleDrop}
          className={`border-2 border-dashed rounded-xl p-8 flex flex-col items-center justify-center text-center transition-colors ${file ? "border-primary bg-primary-container/10" : "border-outline-variant hover:border-primary hover:bg-surface-container-low"}`}
        >
          <span className={`material-symbols-outlined text-[48px] mb-4 ${file ? "text-primary" : "text-outline"}`}>
            {file ? "description" : "upload_file"}
          </span>
          {file ? (
            <div>
              <p className="text-body-lg font-semibold text-on-surface mb-1">{file.name}</p>
              <p className="text-body-sm text-on-surface-variant">{(file.size / 1024).toFixed(1)} KB</p>
            </div>
          ) : (
            <div>
              <p className="text-body-lg text-on-surface font-semibold mb-1">Kéo thả file vào đây</p>
              <p className="text-body-md text-on-surface-variant mb-4">hoặc</p>
              <label className="bg-primary hover:bg-primary/90 text-on-primary px-4 py-2 rounded-lg font-button cursor-pointer transition-colors inline-block">
                Chọn file
                <input type="file" accept=".csv, application/vnd.openxmlformats-officedocument.spreadsheetml.sheet, application/vnd.ms-excel" className="hidden" onChange={(e) => e.target.files && setFile(e.target.files[0])} />
              </label>
            </div>
          )}
        </div>

        <div className="mt-4 flex justify-between items-center">
          <a href="#" className="text-body-sm text-primary hover:underline">Tải file mẫu (.xlsx)</a>
          <div className="flex gap-3">
            <button onClick={onClose} disabled={isUploading} className="px-4 py-2 rounded-lg border border-outline-variant text-on-surface hover:bg-surface-container text-button transition-colors">Hủy</button>
            <button onClick={handleUpload} disabled={!file || isUploading} className="px-4 py-2 rounded-lg bg-primary text-on-primary text-button hover:bg-primary/90 transition-colors flex items-center gap-2 disabled:opacity-60">
              {isUploading ? <span className="material-symbols-outlined text-[16px] animate-spin">progress_activity</span> : "Tải lên"}
            </button>
          </div>
        </div>
      </div>
    </div>
  );
}

// ── Toast ─────────────────────────────────────────────────────
function Toast({ message, type, onClose }: { message: string; type: "success" | "error"; onClose: () => void }) {
  useEffect(() => {
    const t = setTimeout(onClose, 3500);
    return () => clearTimeout(t);
  }, [onClose]);
  return (
    <div className={`fixed bottom-6 right-6 z-50 flex items-center gap-3 px-lg py-md rounded-xl shadow-lg border text-body-md font-semibold transition-all ${type === "success" ? "bg-secondary-container text-on-secondary-container border-secondary-container" : "bg-error-container text-on-error-container border-error-container"}`}>
      <span className="material-symbols-outlined text-[20px]" style={{ fontVariationSettings: "'FILL' 1" }}>
        {type === "success" ? "check_circle" : "error"}
      </span>
      {message}
    </div>
  );
}

// ── Main Page ─────────────────────────────────────────────────
export default function UsersPage() {
  const [users, setUsers] = useState<User[]>([]);
  const [total, setTotal] = useState(0);
  const [page, setPage] = useState(1);
  const [totalPages, setTotalPages] = useState(1);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [search, setSearch] = useState("");
  const [roleFilter, setRoleFilter] = useState<UserRole | "">("");
  const [deleteTarget, setDeleteTarget] = useState<User | null>(null);
  const [isDeleting, setIsDeleting] = useState(false);
  const [showImportModal, setShowImportModal] = useState(false);
  const [toast, setToast] = useState<{ message: string; type: "success" | "error" } | null>(null);

  const searchDebounce = useRef<ReturnType<typeof setTimeout> | null>(null);

  const fetchUsers = useCallback(async (p = 1, q = search, role = roleFilter) => {
    setIsLoading(true);
    setError(null);
    try {
      // Mock data instead of real API call
      const mockUsers: User[] = [
        { id: "1", code: "SV001", name: "Nguyễn Văn An", email: "an.nv@universe.edu.vn", role: "student", status: "active", department: "Công nghệ thông tin", createdAt: "2023-01-01", updatedAt: "2023-01-01" },
        { id: "2", code: "GV001", name: "Trần Thị Bích", email: "bich.tt@universe.edu.vn", role: "lecturer", status: "active", department: "Kinh tế", createdAt: "2023-01-01", updatedAt: "2023-01-01" },
        { id: "3", code: "AD001", name: "Lê Cường (Admin)", email: "admin@universe.edu.vn", role: "admin", status: "active", createdAt: "2023-01-01", updatedAt: "2023-01-01" },
        { id: "4", code: "SV002", name: "Phạm Thu Dung", email: "dung.pt@universe.edu.vn", role: "student", status: "suspended", department: "Ngoại ngữ", createdAt: "2023-01-01", updatedAt: "2023-01-01" },
        { id: "5", code: "SV003", name: "Hoàng Tùng", email: "tung.h@universe.edu.vn", role: "student", status: "graduated", department: "Cơ khí", createdAt: "2023-01-01", updatedAt: "2023-01-01" }
      ];
      
      // Simulating network delay
      await new Promise(resolve => setTimeout(resolve, 600));
      
      let filteredData = mockUsers;
      if (q) {
        filteredData = filteredData.filter(u => 
          u.name.toLowerCase().includes(q.toLowerCase()) || 
          u.code.toLowerCase().includes(q.toLowerCase()) ||
          u.email.toLowerCase().includes(q.toLowerCase())
        );
      }
      if (role) {
        filteredData = filteredData.filter(u => u.role === role);
      }
      
      setUsers(filteredData);
      setTotal(filteredData.length);
      setTotalPages(1);
      setPage(1);
    } catch {
      setError("Không thể tải danh sách người dùng. Vui lòng thử lại.");
    } finally {
      setIsLoading(false);
    }
  }, [search, roleFilter]);

  useEffect(() => {
    fetchUsers(1);
  }, []);

  const handleSearch = (val: string) => {
    setSearch(val);
    if (searchDebounce.current) clearTimeout(searchDebounce.current);
    searchDebounce.current = setTimeout(() => fetchUsers(1, val, roleFilter), 400);
  };

  const handleRoleFilter = (role: UserRole | "") => {
    setRoleFilter(role);
    fetchUsers(1, search, role);
  };

  const handleDelete = async () => {
    if (!deleteTarget) return;
    setIsDeleting(true);
    try {
      await api.delete(`/users/${deleteTarget.id}`);
      setToast({ message: `Đã xóa tài khoản ${deleteTarget.name}`, type: "success" });
      setDeleteTarget(null);
      fetchUsers(page);
    } catch {
      setToast({ message: "Xóa tài khoản thất bại. Vui lòng thử lại.", type: "error" });
    } finally {
      setIsDeleting(false);
    }
  };

  const getInitialColor = (role: UserRole) => {
    if (role === "lecturer") return "bg-tertiary-container/20 text-tertiary";
    if (role === "admin") return "bg-error-container/20 text-error";
    return "bg-primary-container/20 text-primary";
  };

  const roleTabs: { label: string; value: UserRole | "" }[] = [
    { label: "Tất cả", value: "" },
    { label: "Sinh viên", value: "student" },
    { label: "Giảng viên", value: "lecturer" },
    { label: "Admin", value: "admin" },
  ];

  return (
    <>
      {toast && <Toast message={toast.message} type={toast.type} onClose={() => setToast(null)} />}
      {deleteTarget && (
        <DeleteModal
          user={deleteTarget}
          isDeleting={isDeleting}
          onConfirm={handleDelete}
          onCancel={() => setDeleteTarget(null)}
        />
      )}
      {showImportModal && (
        <ImportModal 
          onClose={() => setShowImportModal(false)} 
          onSuccess={() => {
            setShowImportModal(false);
            setToast({ message: "Nhập danh sách người dùng thành công", type: "success" });
            fetchUsers(1);
          }}
        />
      )}

      <main className="flex-1 overflow-y-auto mt-16 p-xl bg-background">
        {/* Header */}
        <div className="flex justify-between items-end mb-lg">
          <div>
            <h2 className="text-h1 text-on-surface">Quản lý người dùng</h2>
            <p className="text-body-md text-on-surface-variant mt-1">
              {isLoading ? "Đang tải..." : `${total.toLocaleString()} người dùng trong hệ thống`}
            </p>
          </div>
          <div className="flex gap-2">
            <button
              onClick={() => fetchUsers(page)}
              className="px-3 py-2 bg-surface border border-outline-variant rounded-lg flex items-center gap-2 text-on-surface hover:bg-surface-container transition-colors text-body-sm"
              title="Làm mới"
            >
              <span className="material-symbols-outlined text-[18px]">refresh</span>
            </button>
            <button
              onClick={() => setShowImportModal(true)}
              className="px-4 py-2 bg-surface border border-outline-variant rounded-lg flex items-center gap-2 text-on-surface hover:bg-surface-container transition-colors text-button"
            >
              <span className="material-symbols-outlined text-[20px]">upload_file</span>
              Nhập từ Excel
            </button>
            <Link
              href="/users/new"
              className="bg-primary hover:bg-surface-tint text-on-primary text-button px-4 py-2 rounded-lg flex items-center gap-2 shadow-sm transition-colors duration-200"
            >
              <span className="material-symbols-outlined text-[20px]">add</span>
              Thêm
            </Link>
          </div>
        </div>

        {/* Data Card */}
        <div className="bg-surface-container-lowest rounded-xl shadow-[var(--shadow-card)] border border-border-muted overflow-hidden flex flex-col">
          {/* Toolbar */}
          <div className="p-md border-b border-border-muted flex flex-col sm:flex-row gap-3 items-start sm:items-center justify-between">
            <div className="relative w-full sm:w-96">
              <span className="material-symbols-outlined absolute left-3 top-1/2 -translate-y-1/2 text-on-surface-variant text-[20px]">search</span>
              <input
                className="w-full pl-10 pr-4 py-2 bg-surface rounded-lg border border-outline-variant focus:border-primary focus:ring-1 focus:ring-primary outline-none transition-all text-body-sm text-on-surface placeholder:text-on-surface-variant/70"
                placeholder="Tìm kiếm theo MSSV/MGV, Tên, Email..."
                type="text"
                value={search}
                onChange={(e) => handleSearch(e.target.value)}
              />
            </div>
            <div className="flex items-center gap-1 bg-surface-container rounded-lg p-1">
              {roleTabs.map((tab) => (
                <button
                  key={tab.value}
                  onClick={() => handleRoleFilter(tab.value)}
                  className={`px-3 py-1.5 rounded-md text-body-sm font-semibold transition-colors ${roleFilter === tab.value ? "bg-primary text-on-primary shadow-sm" : "text-on-surface-variant hover:text-on-surface"}`}
                >
                  {tab.label}
                </button>
              ))}
            </div>
          </div>

          {/* Error state */}
          {error && (
            <div className="p-xl flex flex-col items-center gap-3 text-center">
              <span className="material-symbols-outlined text-[40px] text-error" style={{ fontVariationSettings: "'FILL' 1" }}>error</span>
              <p className="text-body-md text-on-surface-variant">{error}</p>
              <button onClick={() => fetchUsers(page)} className="px-4 py-2 bg-primary text-on-primary rounded-lg text-button">Thử lại</button>
            </div>
          )}

          {/* Table */}
          {!error && (
            <div className="flex-1 overflow-auto">
              <table className="w-full text-left border-collapse">
                <thead className="bg-surface sticky top-0 z-10 border-b border-border-muted">
                  <tr>
                    <th className="py-3 px-4 text-label text-on-surface-variant uppercase tracking-wider">MSSV/MGV</th>
                    <th className="py-3 px-4 text-label text-on-surface-variant uppercase tracking-wider">Họ tên</th>
                    <th className="py-3 px-4 text-label text-on-surface-variant uppercase tracking-wider">Email</th>
                    <th className="py-3 px-4 text-label text-on-surface-variant uppercase tracking-wider">Vai trò</th>
                    <th className="py-3 px-4 text-label text-on-surface-variant uppercase tracking-wider">Khoa</th>
                    <th className="py-3 px-4 text-label text-on-surface-variant uppercase tracking-wider">Trạng thái</th>
                    <th className="py-3 px-4 text-label text-on-surface-variant uppercase tracking-wider text-right">Thao tác</th>
                  </tr>
                </thead>
                <tbody className="divide-y divide-outline-variant/20 text-body-sm text-on-surface">
                  {isLoading
                    ? [...Array(6)].map((_, i) => <SkeletonRow key={i} />)
                    : users.length === 0
                    ? (
                      <tr>
                        <td colSpan={7} className="py-16 text-center text-on-surface-variant text-body-md">
                          <span className="material-symbols-outlined text-[40px] block mb-2">person_search</span>
                          Không tìm thấy người dùng nào
                        </td>
                      </tr>
                    )
                    : users.map((user, idx) => (
                      <tr key={user.id} className={`hover:bg-surface-container-lowest/50 transition-colors ${idx % 2 === 1 ? "bg-surface" : ""}`}>
                        <td className="py-3 px-4 font-medium">{user.code}</td>
                        <td className="py-3 px-4">
                          <div className="flex items-center gap-3">
                            <div className={`w-8 h-8 rounded-full ${getInitialColor(user.role)} flex items-center justify-center font-bold text-sm`}>
                              {user.avatarUrl
                                ? <img src={user.avatarUrl} alt={user.name} className="w-8 h-8 rounded-full object-cover" />
                                : user.name.charAt(0).toUpperCase()}
                            </div>
                            {user.name}
                          </div>
                        </td>
                        <td className="py-3 px-4 text-on-surface-variant">{user.email}</td>
                        <td className="py-3 px-4">{roleLabelMap[user.role]}</td>
                        <td className="py-3 px-4">{user.department ?? "—"}</td>
                        <td className="py-3 px-4">
                          <span className={`inline-flex items-center px-2 py-1 rounded-full text-label text-[10px] ${statusStyleMap[user.status] ?? "bg-surface-container-high text-on-surface-variant"}`}>
                            {statusLabelMap[user.status] ?? user.status}
                          </span>
                        </td>
                        <td className="py-3 px-4 text-right">
                          <Link href={`/users/${user.id}/edit`} className="text-on-surface-variant hover:text-primary p-1 transition-colors inline-block">
                            <span className="material-symbols-outlined text-[18px]">edit</span>
                          </Link>
                          <button onClick={() => setDeleteTarget(user)} className="text-on-surface-variant hover:text-error p-1 ml-1 transition-colors">
                            <span className="material-symbols-outlined text-[18px]">delete</span>
                          </button>
                        </td>
                      </tr>
                    ))}
                </tbody>
              </table>
            </div>
          )}

          {/* Pagination */}
          {!isLoading && !error && totalPages > 1 && (
            <div className="p-md border-t border-border-muted bg-surface flex justify-between items-center">
              <p className="text-body-sm text-on-surface-variant">
                Trang {page} / {totalPages} — {total.toLocaleString()} người dùng
              </p>
              <div className="flex items-center gap-1">
                <button onClick={() => fetchUsers(page - 1)} disabled={page === 1} className="w-8 h-8 rounded flex items-center justify-center border border-outline-variant bg-surface-container-lowest text-on-surface-variant hover:bg-surface-container disabled:opacity-40">
                  <span className="material-symbols-outlined text-[18px]">chevron_left</span>
                </button>
                {[...Array(Math.min(5, totalPages))].map((_, i) => {
                  const p = i + 1;
                  return (
                    <button key={p} onClick={() => fetchUsers(p)} className={`w-8 h-8 rounded flex items-center justify-center text-button ${page === p ? "bg-primary text-on-primary" : "hover:bg-surface-container text-on-surface"}`}>{p}</button>
                  );
                })}
                <button onClick={() => fetchUsers(page + 1)} disabled={page === totalPages} className="w-8 h-8 rounded flex items-center justify-center border border-outline-variant bg-surface-container-lowest text-on-surface-variant hover:bg-surface-container disabled:opacity-40">
                  <span className="material-symbols-outlined text-[18px]">chevron_right</span>
                </button>
              </div>
            </div>
          )}
        </div>
      </main>
    </>
  );
}
