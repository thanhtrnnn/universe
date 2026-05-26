"use client";

import React, { useEffect, useRef } from "react";
import { useNotificationStore } from "@/store/notificationStore";
import type { Notification } from "@/types";

function timeAgo(dateStr: string): string {
  const diff = (Date.now() - new Date(dateStr).getTime()) / 1000;
  if (diff < 60) return "Vừa xong";
  if (diff < 3600) return `${Math.floor(diff / 60)} phút trước`;
  if (diff < 86400) return `${Math.floor(diff / 3600)} giờ trước`;
  return `${Math.floor(diff / 86400)} ngày trước`;
}

const typeIconMap: Record<Notification["type"], string> = {
  system: "settings",
  class: "class",
  grade: "grade",
  attendance: "how_to_reg",
};

const typeColorMap: Record<Notification["type"], string> = {
  system: "bg-tertiary-container text-tertiary",
  class: "bg-primary-container/30 text-primary",
  grade: "bg-secondary-container text-secondary",
  attendance: "bg-error-container text-error",
};

export default function NotificationBell() {
  const { notifications, unreadCount, isOpen, isLoading, fetchNotifications, toggleReadStatus, markAllAsRead, toggleDropdown, closeDropdown } = useNotificationStore();
  const ref = useRef<HTMLDivElement>(null);

  useEffect(() => {
    fetchNotifications();
  }, []);

  // Close on outside click
  useEffect(() => {
    const handler = (e: MouseEvent) => {
      if (ref.current && !ref.current.contains(e.target as Node)) {
        closeDropdown();
      }
    };
    document.addEventListener("mousedown", handler);
    return () => document.removeEventListener("mousedown", handler);
  }, [closeDropdown]);

  return (
    <div className="relative" ref={ref}>
      {/* Bell Button */}
      <button
        id="notification-bell-btn"
        onClick={toggleDropdown}
        className="text-on-surface-variant hover:bg-surface-container rounded-full p-2 transition-colors focus:ring-2 focus:ring-primary/20 relative"
        aria-label="Thông báo"
      >
        <span className="material-symbols-outlined">notifications</span>
        {unreadCount > 0 && (
          <span className="absolute -top-0.5 -right-0.5 w-5 h-5 bg-error text-on-error text-[10px] font-bold rounded-full flex items-center justify-center animate-pulse">
            {unreadCount > 9 ? "9+" : unreadCount}
          </span>
        )}
      </button>

      {/* Dropdown */}
      {isOpen && (
        <div className="absolute right-0 top-12 w-[380px] bg-surface-container-lowest rounded-xl shadow-[var(--shadow-float)] border border-border-muted overflow-hidden z-50 animate-in fade-in slide-in-from-top-2 duration-200">
          {/* Header */}
          <div className="flex items-center justify-between px-lg py-md border-b border-border-muted">
            <h3 className="text-h3 text-on-surface">Thông báo</h3>
            <div className="flex items-center gap-2">
              {unreadCount > 0 && (
                <button onClick={markAllAsRead} className="text-body-sm text-primary hover:underline">
                  Đọc tất cả
                </button>
              )}
              <button onClick={() => fetchNotifications()} className="text-on-surface-variant hover:text-on-surface p-1 rounded transition-colors">
                <span className={`material-symbols-outlined text-[18px] ${isLoading ? "animate-spin" : ""}`}>refresh</span>
              </button>
            </div>
          </div>

          {/* List */}
          <div className="max-h-[380px] overflow-y-auto custom-scrollbar">
            {isLoading && notifications.length === 0 ? (
              <div className="p-lg flex flex-col gap-3">
                {[...Array(3)].map((_, i) => (
                  <div key={i} className="flex gap-3 animate-pulse">
                    <div className="w-8 h-8 rounded-full bg-surface-container-high flex-shrink-0" />
                    <div className="flex-1 space-y-2">
                      <div className="h-3 bg-surface-container-high rounded w-3/4" />
                      <div className="h-3 bg-surface-container-high rounded w-full" />
                    </div>
                  </div>
                ))}
              </div>
            ) : notifications.length === 0 ? (
              <div className="py-12 flex flex-col items-center gap-2 text-on-surface-variant">
                <span className="material-symbols-outlined text-[40px]" style={{ fontVariationSettings: "'FILL' 1" }}>notifications_none</span>
                <p className="text-body-sm">Chưa có thông báo nào</p>
              </div>
            ) : (
              notifications.map((n) => (
                <div
                  key={n.id}
                  className={`w-full flex gap-3 px-lg py-md text-left hover:bg-surface-container-low transition-colors border-b border-border-muted/50 last:border-0 ${!n.isRead ? "bg-primary/5" : ""}`}
                >
                  <div className={`w-8 h-8 rounded-full flex-shrink-0 flex items-center justify-center ${typeColorMap[n.type]}`}>
                    <span className="material-symbols-outlined text-[16px]" style={{ fontVariationSettings: "'FILL' 1" }}>
                      {typeIconMap[n.type]}
                    </span>
                  </div>
                  <div className="flex-1 min-w-0 pr-2">
                    <p className={`text-body-sm text-on-surface line-clamp-1 ${!n.isRead ? "font-semibold" : ""}`}>{n.title}</p>
                    <p className="text-body-sm text-on-surface-variant line-clamp-2 mt-0.5">{n.content}</p>
                    <p className="text-label text-outline mt-1">{timeAgo(n.createdAt)}</p>
                  </div>
                  <div className="flex-shrink-0 flex items-start pt-1">
                    <button 
                      onClick={(e) => {
                        e.stopPropagation();
                        toggleReadStatus(n.id, n.isRead);
                      }}
                      title={n.isRead ? "Đánh dấu chưa đọc" : "Đánh dấu đã đọc"}
                      className="p-1 rounded-full text-on-surface-variant hover:text-primary hover:bg-primary-container/20 transition-colors"
                    >
                      {n.isRead ? (
                        <span className="material-symbols-outlined text-[18px]">drafts</span>
                      ) : (
                        <div className="w-2.5 h-2.5 bg-primary rounded-full m-1" />
                      )}
                    </button>
                  </div>
                </div>
              ))
            )}
          </div>

          {/* Footer */}
          <div className="border-t border-border-muted px-lg py-sm">
            <button className="w-full text-center text-body-sm text-primary hover:underline py-1">
              Xem tất cả thông báo
            </button>
          </div>
        </div>
      )}
    </div>
  );
}
