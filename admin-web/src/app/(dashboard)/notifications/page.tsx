"use client";
import React, { useState } from "react";
import api from "@/lib/api";

export default function NotificationsPage() {
  const [title, setTitle] = useState("");
  const [content, setContent] = useState("");
  const [selectedTargets, setSelectedTargets] = useState<string[]>([]);
  const [isSending, setIsSending] = useState(false);
  const [sent, setSent] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const targetOptions = [
    { label: "Tất cả người dùng", value: "all" },
    { label: "Sinh viên", value: "students" },
    { label: "Giảng viên", value: "lecturers" },
    { label: "Theo khoa chuyên môn", value: "department" },
  ];

  const toggleTarget = (val: string) => {
    setSelectedTargets((prev) =>
      prev.includes(val) ? prev.filter((v) => v !== val) : [...prev, val]
    );
  };

  const handleSend = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!title.trim() || !content.trim() || selectedTargets.length === 0) {
      setError("Vui lòng điền đầy đủ thông tin và chọn đối tượng nhận.");
      return;
    }
    setIsSending(true);
    setError(null);
    try {
      await api.post("/notifications", {
        title,
        content,
        type: "system",
        targetType: selectedTargets.includes("all") ? "all" : "department",
        targets: selectedTargets,
      });
      setSent(true);
      setTitle("");
      setContent("");
      setSelectedTargets([]);
      setTimeout(() => setSent(false), 4000);
    } catch {
      setError("Gửi thông báo thất bại. Vui lòng thử lại.");
    } finally {
      setIsSending(false);
    }
  };


  return (
    <main className="flex-1 pt-20 px-xl pb-xl bg-background overflow-y-auto">
      {/* Success Toast */}
      {sent && (
        <div className="fixed bottom-6 right-6 z-50 flex items-center gap-3 px-lg py-md rounded-xl shadow-lg bg-secondary-container text-on-secondary-container border border-secondary-container text-body-md font-semibold">
          <span className="material-symbols-outlined text-[20px]" style={{ fontVariationSettings: "'FILL' 1" }}>check_circle</span>
          Thông báo đã được gửi thành công!
        </div>
      )}
      {/* Page Header */}
      <div className="mb-lg">
        <h1 className="font-h1 text-h1 text-on-surface">Gửi thông báo hệ thống</h1>
        <p className="font-body-md text-body-md text-on-surface-variant mt-1">
          Soạn thảo và gửi thông báo quan trọng đến người dùng trong hệ thống UniVerse.
        </p>
      </div>

      {/* Content Grid */}
      <div className="grid grid-cols-12 gap-container_gutter items-start">
        {/* Left Column: Form (8 cols) */}
        <div className="col-span-12 lg:col-span-8">
          <div className="bg-surface-container-lowest rounded-xl shadow-[0px_4px_20px_rgba(26,26,26,0.05)] border border-[#EEEDFE] p-lg">
            <form className="space-y-lg" onSubmit={handleSend}>
              {error && (
                <div className="flex items-center gap-2 bg-error-container text-on-error-container px-md py-sm rounded-lg text-body-sm">
                  <span className="material-symbols-outlined text-[18px]" style={{ fontVariationSettings: "'FILL' 1" }}>error</span>
                  {error}
                </div>
              )}
              {/* Field: Tiêu đề */}
              <div>
                <label className="block font-label text-label text-on-surface mb-sm">
                  Tiêu đề thông báo <span className="text-error">*</span>
                </label>
                <input
                  className="w-full px-md py-sm bg-surface rounded-lg border border-outline-variant focus:border-primary focus:ring-1 focus:ring-primary outline-none transition-colors font-body-md text-body-md text-on-surface placeholder:text-outline"
                  placeholder="Nhập tiêu đề ngắn gọn, rõ ràng"
                  type="text"
                  value={title}
                  onChange={(e) => setTitle(e.target.value)}
                />
              </div>

              {/* Field: Nội dung */}
              <div>
                <label className="block font-label text-label text-on-surface mb-sm">
                  Nội dung <span className="text-error">*</span>
                </label>
                <textarea
                  className="w-full px-md py-sm bg-surface rounded-lg border border-outline-variant focus:border-primary focus:ring-1 focus:ring-primary outline-none transition-colors font-body-md text-body-md text-on-surface placeholder:text-outline resize-y min-h-[160px]"
                  placeholder="Nhập nội dung chi tiết của thông báo..."
                  rows={6}
                  value={content}
                  onChange={(e) => setContent(e.target.value)}
                ></textarea>
              </div>

              {/* Field: Đối tượng nhận */}
              <div>
                <label className="block font-label text-label text-on-surface mb-md">
                  Chọn đối tượng nhận <span className="text-error">*</span>
                </label>
                <div className="grid grid-cols-1 sm:grid-cols-2 gap-md">
                  {targetOptions.map(({ label, value }) => (
                    <label key={value} className={`flex items-center gap-3 p-3 rounded-lg border cursor-pointer transition-colors ${selectedTargets.includes(value) ? "border-primary bg-primary/5" : "border-outline-variant hover:bg-surface-container-low"}`}>
                      <input
                        type="checkbox"
                        className="w-5 h-5 rounded text-primary border-outline-variant focus:ring-primary"
                        checked={selectedTargets.includes(value)}
                        onChange={() => toggleTarget(value)}
                      />
                      <span className="font-body-md text-body-md text-on-surface">{label}</span>
                    </label>
                  ))}
                </div>
              </div>

              {/* Field: Attachment */}
              <div>
                <label className="block font-label text-label text-on-surface mb-sm">
                  Đính kèm tài liệu (Tùy chọn)
                </label>
                <div className="mt-2 flex justify-center rounded-lg border border-dashed border-outline-variant px-6 py-10 hover:bg-surface-container-low transition-colors cursor-pointer">
                  <div className="text-center">
                    <span className="material-symbols-outlined text-4xl text-outline mb-2">cloud_upload</span>
                    <div className="mt-4 flex text-sm leading-6 text-on-surface-variant justify-center">
                      <label className="relative cursor-pointer rounded-md font-button text-button text-primary focus-within:outline-none focus-within:ring-2 focus-within:ring-primary focus-within:ring-offset-2 hover:text-primary-container">
                        <span>Tải tệp lên</span>
                        <input className="sr-only" name="file-upload" type="file" />
                      </label>
                      <p className="pl-1">hoặc kéo thả vào đây</p>
                    </div>
                    <p className="text-xs leading-5 text-outline mt-1">PDF, DOCX, JPG tối đa 10MB</p>
                  </div>
                </div>
              </div>

              {/* Actions */}
              <div className="pt-sm flex justify-end gap-md">
                <button
                  className="px-lg py-sm rounded-lg font-button text-button text-on-surface border border-outline-variant hover:bg-surface-container-low transition-colors"
                  type="button"
                  onClick={() => { setTitle(""); setContent(""); setSelectedTargets([]); setError(null); }}
                >
                  Hủy bỏ
                </button>
                <button
                  className="px-lg py-sm rounded-lg font-button text-button bg-[#6C63FF] text-white hover:bg-[#5a52d9] shadow-sm flex items-center gap-2 transition-colors disabled:opacity-60"
                  type="submit"
                  disabled={isSending}
                >
                  {isSending
                    ? <span className="material-symbols-outlined text-[18px] animate-spin">progress_activity</span>
                    : <span className="material-symbols-outlined text-[18px]">send</span>}
                  {isSending ? "Đang gửi..." : "Gửi thông báo"}
                </button>
              </div>
            </form>
          </div>
        </div>

        {/* Right Column: Preview Card (4 cols) */}
        <div className="col-span-12 lg:col-span-4 lg:sticky lg:top-24">
          <div className="bg-surface-container-lowest rounded-xl shadow-[0px_10px_30px_rgba(26,26,26,0.12)] border border-[#EEEDFE] overflow-hidden flex flex-col">
            {/* Preview Header */}
            <div className="bg-surface-container px-lg py-sm border-b border-[#EEEDFE] flex items-center gap-2">
              <span className="material-symbols-outlined text-outline text-[18px]">visibility</span>
              <h3 className="font-h3 text-h3 text-on-surface">Xem trước</h3>
            </div>
            {/* Simulated Notification UI */}
            <div className="p-lg flex-1 bg-surface-bright">
              <div className="flex gap-4">
                <div className="w-10 h-10 rounded-full bg-[#6C63FF]/10 flex items-center justify-center flex-shrink-0">
                  <span className="material-symbols-outlined text-[#6C63FF]">campaign</span>
                </div>
                <div className="flex-1 min-w-0">
                  <div className="flex items-start justify-between gap-4 mb-1">
                    <h4 className="font-button text-button text-on-surface line-clamp-2">
                      {title || "Tiêu đề thông báo sẽ hiển thị ở đây"}
                    </h4>
                    <span className="font-body-sm text-body-sm text-outline flex-shrink-0">Vừa xong</span>
                  </div>
                  <p className="font-body-md text-body-md text-on-surface-variant line-clamp-3">
                    {content || "Nội dung chi tiết của thông báo sẽ được hiển thị cắt ngắn tại đây trên giao diện của người dùng. Họ có thể nhấp vào để xem toàn bộ chi tiết và tải xuống tệp đính kèm."}
                  </p>
                  {/* Simulated Attachment */}
                  <div className="mt-4 flex items-center gap-2 p-2 rounded border border-outline-variant bg-surface-container-lowest w-max">
                    <span className="material-symbols-outlined text-error text-[20px]">picture_as_pdf</span>
                    <span className="font-body-sm text-body-sm text-on-surface">Tài liệu đính kèm.pdf</span>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </main>
  );
}

