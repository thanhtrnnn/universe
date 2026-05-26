"use client";

import React, { useState } from "react";

export default function CourseRegistrationPage() {
  const [searchTerm, setSearchTerm] = useState("");
  const [filterDept, setFilterDept] = useState("all");
  const [filterClass, setFilterClass] = useState("all");

  const availableCourses = [
    { id: 1, classCode: "INT1332_01", courseCode: "INT1332", name: "Lập trình Web", credits: 3, lecturer: "Nguyễn Tuấn A", registered: 45, limit: 60, schedule: "Thứ 4 (1-3) - 503A1" },
    { id: 2, classCode: "INT1332_02", courseCode: "INT1332", name: "Lập trình Web", credits: 3, lecturer: "Trần Văn B", registered: 60, limit: 60, schedule: "Thứ 5 (7-9) - 402A2" },
    { id: 3, classCode: "INT1416_01", courseCode: "INT1416", name: "Hệ quản trị CSDL", credits: 3, lecturer: "Lê Thị C", registered: 20, limit: 80, schedule: "Thứ 2 (4-6) - 105A3" },
    { id: 6, classCode: "BAS1152_04", courseCode: "BAS1152", name: "Chủ nghĩa xã hội khoa học", credits: 2, lecturer: "P.T.Khánh", registered: 85, limit: 90, schedule: "Thứ 4 (3-4) - 503A1" },
  ];

  const [registeredCourses, setRegisteredCourses] = useState([
    { id: 4, classCode: "INT1315_03", courseCode: "INT1315", name: "Hệ điều hành", credits: 3, lecturer: "Phạm D", schedule: "Thứ 6 (1-3) - 201A2", status: "Thành công" },
    { id: 5, classCode: "BAS1204_01", courseCode: "BAS1204", name: "Giải tích 2", credits: 3, lecturer: "Đinh E", schedule: "Thứ 3 (4-6) - 302A1", status: "Chờ duyệt" },
  ]);

  const filteredCourses = availableCourses.filter(c => 
    (c.name.toLowerCase().includes(searchTerm.toLowerCase()) || c.courseCode.toLowerCase().includes(searchTerm.toLowerCase()))
  );

  return (
    <main className="flex-1 overflow-y-auto mt-16 p-xl bg-background">
      <div className="mb-lg flex flex-col md:flex-row justify-between items-start md:items-end gap-4">
        <div>
          <h1 className="text-h1 text-on-surface mb-2">Đăng ký tín chỉ</h1>
          <p className="text-body-md text-on-surface-variant">
            Học kỳ 1 - Năm học 2026-2027 (Trạng thái: <span className="text-green-600 font-semibold">Đang mở đăng ký</span>)
          </p>
        </div>
        <div className="bg-surface rounded-xl p-4 border border-border-muted shadow-sm flex items-center gap-4">
          <div className="w-12 h-12 rounded-full bg-primary/10 flex items-center justify-center text-primary">
            <span className="material-symbols-outlined text-[24px]">school</span>
          </div>
          <div>
            <p className="text-body-sm text-on-surface-variant">Tổng tín chỉ đã đăng ký</p>
            <p className="text-h2 text-on-surface leading-none mt-1">
              {registeredCourses.reduce((sum, c) => sum + c.credits, 0)} <span className="text-body-md text-on-surface-variant font-normal">TC</span>
            </p>
          </div>
        </div>
      </div>

      <div className="grid grid-cols-1 xl:grid-cols-3 gap-6">
        
        {/* Left column: Search and Available Courses */}
        <div className="xl:col-span-2 flex flex-col gap-6">
          
          {/* Filter Section */}
          <div className="bg-surface rounded-2xl p-6 border border-border-muted shadow-sm">
            <h3 className="text-body-lg font-semibold text-on-surface mb-4 flex items-center gap-2">
              <span className="material-symbols-outlined">filter_alt</span> Bộ lọc tìm kiếm
            </h3>
            <div className="grid grid-cols-1 md:grid-cols-4 gap-4">
              <div>
                <label className="block text-body-sm text-on-surface-variant mb-1">Khoa / Viện quản lý</label>
                <div className="relative">
                  <select 
                    value={filterDept}
                    onChange={(e) => setFilterDept(e.target.value)}
                    className="w-full appearance-none px-4 py-2 bg-surface-container-lowest border border-outline-variant rounded-lg text-body-md focus:border-primary outline-none transition-colors pr-8"
                  >
                    <option value="all">-- Tất cả khoa --</option>
                    <option value="cntt">Khoa Công nghệ thông tin 1</option>
                    <option value="attt">Khoa An toàn thông tin</option>
                    <option value="cb">Khoa Cơ bản 1</option>
                  </select>
                  <span className="material-symbols-outlined absolute right-2 top-1/2 -translate-y-1/2 text-on-surface-variant pointer-events-none">expand_more</span>
                </div>
              </div>
              <div>
                <label className="block text-body-sm text-on-surface-variant mb-1">Lớp</label>
                <div className="relative">
                  <select 
                    value={filterClass}
                    onChange={(e) => setFilterClass(e.target.value)}
                    className="w-full appearance-none px-4 py-2 bg-surface-container-lowest border border-outline-variant rounded-lg text-body-md focus:border-primary outline-none transition-colors pr-8"
                  >
                    <option value="all">-- Tất cả lớp --</option>
                    <option value="d23cqcn01">D23CQCN01-B</option>
                    <option value="d23cqcn02">D23CQCN02-B</option>
                    <option value="d23cqat01">D23CQAT01-B</option>
                  </select>
                  <span className="material-symbols-outlined absolute right-2 top-1/2 -translate-y-1/2 text-on-surface-variant pointer-events-none">expand_more</span>
                </div>
              </div>
              <div className="md:col-span-2">
                <label className="block text-body-sm text-on-surface-variant mb-1">Mã / Tên môn học</label>
                <div className="relative">
                  <span className="material-symbols-outlined absolute left-3 top-1/2 -translate-y-1/2 text-on-surface-variant">search</span>
                  <input 
                    type="text" 
                    value={searchTerm}
                    onChange={(e) => setSearchTerm(e.target.value)}
                    placeholder="Nhập mã môn hoặc tên môn học..." 
                    className="w-full pl-10 pr-4 py-2 bg-surface-container-lowest border border-outline-variant rounded-lg text-body-md focus:border-primary outline-none transition-colors"
                  />
                </div>
              </div>
            </div>
          </div>

          {/* Available Courses Table */}
          <div className="bg-surface rounded-2xl p-0 border border-border-muted shadow-sm overflow-hidden flex flex-col">
            <div className="p-4 border-b border-border-muted bg-surface-container-lowest flex justify-between items-center">
              <h3 className="text-body-lg font-semibold text-on-surface flex items-center gap-2">
                <span className="material-symbols-outlined text-primary">list_alt</span> Lớp học phần đang mở
              </h3>
              <span className="text-xs bg-primary/10 text-primary px-3 py-1 rounded-full font-semibold">
                {filteredCourses.length} kết quả
              </span>
            </div>
            
            <div className="overflow-x-auto">
              <table className="w-full text-left min-w-[800px] border-collapse">
                <thead className="bg-surface-container-low border-b-2 border-primary/20">
                  <tr>
                    <th className="py-3 px-4 font-semibold text-xs text-on-surface-variant uppercase tracking-wider">Mã LHP</th>
                    <th className="py-3 px-4 font-semibold text-xs text-on-surface-variant uppercase tracking-wider">Tên môn học</th>
                    <th className="py-3 px-4 font-semibold text-xs text-on-surface-variant uppercase tracking-wider text-center">TC</th>
                    <th className="py-3 px-4 font-semibold text-xs text-on-surface-variant uppercase tracking-wider">Lịch học</th>
                    <th className="py-3 px-4 font-semibold text-xs text-on-surface-variant uppercase tracking-wider text-center">Sĩ số</th>
                    <th className="py-3 px-4 font-semibold text-xs text-on-surface-variant uppercase tracking-wider text-center w-24">Thao tác</th>
                  </tr>
                </thead>
                <tbody className="text-sm">
                  {filteredCourses.map((c) => {
                    const isFull = c.registered >= c.limit;
                    return (
                      <tr key={c.id} className="border-b border-outline-variant/20 hover:bg-surface-container-lowest transition-colors">
                        <td className="py-3 px-4 font-medium text-on-surface">{c.classCode}</td>
                        <td className="py-3 px-4 text-on-surface">
                          <div>{c.name}</div>
                          <div className="text-xs text-on-surface-variant mt-0.5">Mã môn: {c.courseCode} | GV: {c.lecturer}</div>
                        </td>
                        <td className="py-3 px-4 text-on-surface text-center font-medium">{c.credits}</td>
                        <td className="py-3 px-4 text-on-surface text-xs">{c.schedule}</td>
                        <td className="py-3 px-4 text-center">
                          <div className="flex flex-col items-center">
                            <span className="font-medium text-on-surface">{c.registered}/{c.limit}</span>
                            <div className="w-16 h-1.5 bg-surface-container-high rounded-full mt-1 overflow-hidden">
                              <div className={`h-full rounded-full ${isFull ? 'bg-error' : 'bg-green-500'}`} style={{ width: `${(c.registered / c.limit) * 100}%` }}></div>
                            </div>
                          </div>
                        </td>
                        <td className="py-3 px-4 text-center">
                          <button 
                            disabled={isFull}
                            className={`w-full py-1.5 rounded-lg text-xs font-semibold transition-colors shadow-sm ${
                              isFull 
                              ? 'bg-surface-variant/50 text-on-surface-variant cursor-not-allowed shadow-none' 
                              : 'bg-primary text-on-primary hover:bg-primary/90'
                            }`}
                          >
                            {isFull ? 'Đã đầy' : 'Đăng ký'}
                          </button>
                        </td>
                      </tr>
                    );
                  })}
                  {filteredCourses.length === 0 && (
                    <tr>
                      <td colSpan={6} className="py-12 text-center text-on-surface-variant">
                        <span className="material-symbols-outlined text-[48px] opacity-20 mb-2">search_off</span>
                        <p>Không tìm thấy lớp học phần phù hợp.</p>
                      </td>
                    </tr>
                  )}
                </tbody>
              </table>
            </div>
          </div>
        </div>

        {/* Right column: Registered Courses */}
        <div className="xl:col-span-1">
          <div className="bg-surface rounded-2xl p-0 border border-border-muted shadow-sm overflow-hidden h-full flex flex-col">
            <div className="p-4 border-b border-border-muted bg-tertiary-container/10 flex justify-between items-center">
              <h3 className="text-body-lg font-semibold text-on-surface flex items-center gap-2">
                <span className="material-symbols-outlined text-tertiary">check_circle</span> LHP đã đăng ký
              </h3>
            </div>
            
            <div className="p-4 flex-1 overflow-y-auto flex flex-col gap-4">
              {registeredCourses.map((c) => (
                <div key={c.id} className="p-4 rounded-xl border border-outline-variant/40 bg-background shadow-sm hover:border-outline-variant transition-colors relative group">
                  <div className="flex justify-between items-start mb-2">
                    <span className="text-xs font-bold bg-surface-container px-2 py-0.5 rounded text-on-surface-variant">{c.classCode}</span>
                    <span className={`text-[10px] font-bold px-2 py-0.5 rounded-full uppercase ${c.status === 'Thành công' ? 'bg-green-100 text-green-700' : 'bg-yellow-100 text-yellow-700'}`}>
                      {c.status}
                    </span>
                  </div>
                  <h4 className="text-body-md font-bold text-on-surface mb-1 leading-snug">{c.name}</h4>
                  <div className="flex items-center justify-between text-xs text-on-surface-variant mt-3">
                    <span className="flex items-center gap-1"><span className="material-symbols-outlined text-[14px]">calendar_clock</span> {c.schedule.split(' - ')[0]}</span>
                    <span className="font-semibold text-primary">{c.credits} TC</span>
                  </div>
                  <button className="absolute top-2 right-2 p-1.5 bg-surface-container rounded-full text-error opacity-0 group-hover:opacity-100 transition-opacity hover:bg-error/10" title="Hủy đăng ký">
                    <span className="material-symbols-outlined text-[16px]">close</span>
                  </button>
                </div>
              ))}
              
              {registeredCourses.length === 0 && (
                <div className="flex flex-col items-center justify-center text-center h-40 text-on-surface-variant">
                  <span className="material-symbols-outlined text-[32px] opacity-30 mb-2">inbox</span>
                  <p className="text-sm">Bạn chưa đăng ký lớp học phần nào.</p>
                </div>
              )}
            </div>

            <div className="p-4 border-t border-border-muted bg-surface-container-lowest">
              <button className="w-full py-2 bg-surface-container border border-outline-variant rounded-lg text-sm font-semibold text-on-surface hover:bg-surface-container-high transition-colors flex items-center justify-center gap-2">
                <span className="material-symbols-outlined text-[18px]">calendar_month</span> Xem thời khóa biểu dự kiến
              </button>
            </div>
          </div>
        </div>

      </div>
    </main>
  );
}
