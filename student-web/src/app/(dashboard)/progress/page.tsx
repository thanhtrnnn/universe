"use client";

import React from "react";

export default function AcademicProgressPage() {
  return (
    <main className="flex-1 overflow-y-auto mt-16 p-xl bg-background">
      <div className="mb-lg">
        <h1 className="text-h1 text-on-surface">Tiến độ học tập</h1>
        <p className="text-body-md text-on-surface-variant mt-1">
          Theo dõi hành trình hoàn thành chương trình đào tạo của bạn
        </p>
      </div>

      <div className="grid grid-cols-1 md:grid-cols-3 gap-6 mb-8">
        {/* GPA Card */}
        <div className="bg-surface rounded-2xl p-6 border border-border-muted shadow-sm flex flex-col items-center justify-center relative overflow-hidden group">
          <div className="absolute inset-0 bg-primary/5 opacity-0 group-hover:opacity-100 transition-opacity"></div>
          <span className="material-symbols-outlined text-primary text-[32px] mb-2">school</span>
          <p className="text-body-sm text-on-surface-variant font-medium">Điểm trung bình tích lũy</p>
          <div className="flex items-end gap-1 mt-2">
            <span className="text-[40px] font-bold text-on-surface leading-none">3.45</span>
            <span className="text-body-md text-on-surface-variant mb-1">/ 4.0</span>
          </div>
          <div className="mt-4 px-3 py-1 bg-green-500/10 text-green-700 rounded-full text-xs font-semibold">
            Xếp loại: Giỏi
          </div>
        </div>

        {/* Total Credits Card */}
        <div className="bg-surface rounded-2xl p-6 border border-border-muted shadow-sm col-span-1 md:col-span-2">
          <div className="flex justify-between items-start mb-4">
            <div>
              <h3 className="text-body-lg font-semibold text-on-surface">Tín chỉ tích lũy</h3>
              <p className="text-body-sm text-on-surface-variant">Tổng số tín chỉ yêu cầu tốt nghiệp: 150</p>
            </div>
            <div className="w-12 h-12 rounded-full bg-primary/10 flex items-center justify-center text-primary">
              <span className="material-symbols-outlined">donut_large</span>
            </div>
          </div>
          
          <div className="flex justify-between items-end mb-2">
            <span className="text-[32px] font-bold text-primary leading-none">95</span>
            <span className="text-body-md text-on-surface font-medium">63.3% Hoàn thành</span>
          </div>
          
          <div className="w-full h-3 bg-surface-container-high rounded-full overflow-hidden">
            <div className="h-full bg-primary rounded-full" style={{ width: "63.3%" }}></div>
          </div>
          
          <div className="grid grid-cols-3 gap-4 mt-6">
            <div>
              <p className="text-xs text-on-surface-variant mb-1">Đại cương</p>
              <p className="text-body-md font-bold text-on-surface">40 / 45</p>
            </div>
            <div>
              <p className="text-xs text-on-surface-variant mb-1">Cơ sở ngành</p>
              <p className="text-body-md font-bold text-on-surface">35 / 40</p>
            </div>
            <div>
              <p className="text-xs text-on-surface-variant mb-1">Chuyên ngành</p>
              <p className="text-body-md font-bold text-on-surface">20 / 65</p>
            </div>
          </div>
        </div>
      </div>

      <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
        {/* Semester Progress Chart */}
        <div className="bg-surface rounded-2xl p-6 border border-border-muted shadow-sm">
          <h3 className="text-body-lg font-semibold text-on-surface mb-6">Biểu đồ tiến độ (GPA theo kỳ)</h3>
          
          <div className="h-[200px] flex items-end justify-between gap-2 px-2 relative">
            {/* Guide lines */}
            <div className="absolute inset-0 flex flex-col justify-between z-0">
              <div className="border-b border-dashed border-border-muted w-full h-0"></div>
              <div className="border-b border-dashed border-border-muted w-full h-0"></div>
              <div className="border-b border-dashed border-border-muted w-full h-0"></div>
              <div className="border-b border-dashed border-border-muted w-full h-0"></div>
              <div className="border-b border-dashed border-border-muted w-full h-0"></div>
            </div>
            
            {/* Bars */}
            {[
              { label: "HK1", val: 3.2, color: "bg-tertiary/60" },
              { label: "HK2", val: 3.5, color: "bg-tertiary/80" },
              { label: "HK3", val: 3.4, color: "bg-primary/70" },
              { label: "HK4", val: 3.6, color: "bg-primary/90" },
              { label: "HK5", val: 3.7, color: "bg-primary" }
            ].map((item, i) => (
              <div key={i} className="flex flex-col items-center flex-1 z-10 group">
                <span className="text-xs font-semibold text-on-surface-variant mb-2 opacity-0 group-hover:opacity-100 transition-opacity">{item.val}</span>
                <div className={`w-full max-w-[40px] rounded-t-lg ${item.color} transition-all duration-500 hover:brightness-110`} style={{ height: `${(item.val / 4) * 100}%` }}></div>
                <span className="text-xs text-on-surface-variant mt-3">{item.label}</span>
              </div>
            ))}
          </div>
        </div>

        {/* Next Steps / Recommendations */}
        <div className="bg-surface rounded-2xl p-6 border border-border-muted shadow-sm">
          <h3 className="text-body-lg font-semibold text-on-surface mb-4">Gợi ý học tập</h3>
          <div className="space-y-4">
            <div className="p-4 rounded-xl bg-surface-container-low border border-outline-variant/30 flex gap-4 items-start">
              <div className="w-10 h-10 rounded-full bg-secondary/10 text-secondary flex items-center justify-center shrink-0">
                <span className="material-symbols-outlined">warning</span>
              </div>
              <div>
                <h4 className="text-body-md font-semibold text-on-surface">Học phần tiên quyết chưa đạt</h4>
                <p className="text-body-sm text-on-surface-variant mt-1">
                  Môn "Mạng máy tính" là tiên quyết cho 2 môn chuyên ngành. Đề nghị đăng ký học lại vào kỳ tới.
                </p>
                <button className="mt-3 text-sm font-semibold text-primary hover:underline">Xem chi tiết</button>
              </div>
            </div>
            
            <div className="p-4 rounded-xl bg-primary/5 border border-primary/20 flex gap-4 items-start">
              <div className="w-10 h-10 rounded-full bg-primary/10 text-primary flex items-center justify-center shrink-0">
                <span className="material-symbols-outlined">task_alt</span>
              </div>
              <div>
                <h4 className="text-body-md font-semibold text-on-surface">Chuẩn bị thực tập cơ sở</h4>
                <p className="text-body-sm text-on-surface-variant mt-1">
                  Bạn đã đủ điều kiện số tín chỉ để đăng ký môn Thực tập cơ sở. Nhớ theo dõi lịch đăng ký.
                </p>
              </div>
            </div>
          </div>
        </div>
      </div>
    </main>
  );
}
