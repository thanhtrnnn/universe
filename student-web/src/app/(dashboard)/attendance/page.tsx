"use client";

import React, { useState } from "react";

export default function AttendancePage() {
  const [scanning, setScanning] = useState(false);

  return (
    <main className="flex-1 pt-24 px-xl pb-xl bg-background overflow-y-auto">
      <div className="mb-lg">
        <h1 className="font-h1 text-h1 text-on-background">Điểm danh thông minh</h1>
      </div>

      <div className="grid grid-cols-1 lg:grid-cols-2 gap-8">
        <div className="bg-surface-container-lowest rounded-xl shadow-card p-8 flex flex-col items-center border border-outline-variant/30">
          <h2 className="font-h3 text-h3 text-on-surface mb-6 text-center">Quét mã QR để điểm danh</h2>
          
          <div className="w-64 h-64 border-4 border-dashed border-primary rounded-xl flex items-center justify-center relative overflow-hidden mb-6 bg-surface-container">
            {scanning ? (
              <div className="absolute inset-0 bg-primary/10 flex flex-col items-center justify-center">
                <div className="w-full h-1 bg-primary animate-pulse absolute top-1/2 -translate-y-1/2 shadow-[0_0_15px_rgba(37,99,235,0.8)]"></div>
                <span className="material-symbols-outlined text-primary text-4xl mb-2 animate-bounce">qr_code_scanner</span>
                <p className="text-primary font-button">Đang tìm mã...</p>
              </div>
            ) : (
              <div className="text-on-surface-variant flex flex-col items-center">
                <span className="material-symbols-outlined text-6xl mb-2 opacity-50">qr_code_2</span>
                <p>Nhấn nút bên dưới để bắt đầu</p>
              </div>
            )}
          </div>

          <button 
            onClick={() => setScanning(!scanning)}
            className={`font-button text-button px-6 py-3 rounded-lg w-full flex items-center justify-center gap-2 transition-colors ${scanning ? 'bg-error text-on-error hover:bg-error/90' : 'bg-primary text-on-primary hover:bg-primary/90'}`}
          >
            <span className="material-symbols-outlined">{scanning ? 'stop' : 'camera_alt'}</span>
            {scanning ? 'Dừng quét' : 'Bật Camera'}
          </button>

          <div className="mt-6 flex items-start gap-3 bg-secondary-container/30 p-4 rounded-lg border border-secondary-container">
            <span className="material-symbols-outlined text-secondary mt-0.5">location_on</span>
            <div>
              <p className="font-button text-button text-on-surface mb-1">Xác thực vị trí (Geo-fencing)</p>
              <p className="font-body-sm text-body-sm text-on-surface-variant">Hệ thống sẽ tự động ghi nhận tọa độ GPS của bạn để đối chiếu với tọa độ phòng học. Vui lòng cấp quyền truy cập vị trí khi được hỏi.</p>
            </div>
          </div>
        </div>

        <div className="bg-surface-container-lowest rounded-xl shadow-card p-6 border border-outline-variant/30 flex flex-col">
          <h2 className="font-h3 text-h3 text-on-surface mb-4">Lịch sử điểm danh gần đây</h2>
          
          <div className="flex-1 flex flex-col gap-4">
            <div className="flex items-center justify-between p-4 rounded-lg bg-surface border border-outline-variant/30">
              <div className="flex items-start gap-3">
                <div className="w-10 h-10 rounded-full bg-secondary-container text-on-secondary-container flex items-center justify-center">
                  <span className="material-symbols-outlined">check</span>
                </div>
                <div>
                  <h4 className="font-button text-button text-on-surface">Nhập môn CNPM</h4>
                  <p className="font-body-sm text-body-sm text-on-surface-variant mt-1">20/05/2026 • 07:15</p>
                </div>
              </div>
              <span className="font-label text-label text-secondary bg-secondary-container px-2 py-1 rounded">Thành công</span>
            </div>

            <div className="flex items-center justify-between p-4 rounded-lg bg-surface border border-outline-variant/30">
              <div className="flex items-start gap-3">
                <div className="w-10 h-10 rounded-full bg-error-container text-on-error-container flex items-center justify-center">
                  <span className="material-symbols-outlined">close</span>
                </div>
                <div>
                  <h4 className="font-button text-button text-on-surface">Thực tập cơ sở</h4>
                  <p className="font-body-sm text-body-sm text-on-surface-variant mt-1">19/05/2026 • 13:40</p>
                </div>
              </div>
              <div className="flex flex-col items-end">
                <span className="font-label text-label text-error bg-error-container px-2 py-1 rounded mb-1">Thất bại</span>
                <span className="font-body-sm text-body-sm text-error">Sai vị trí (Ngoài 50m)</span>
              </div>
            </div>
          </div>
        </div>
      </div>
    </main>
  );
}
