"use client";

import React, { useState } from "react";

interface Room {
  id: string;
  name: string;
  building: string;
  capacity: number;
  latitude: number | null;
  longitude: number | null;
}

const mockRooms: Room[] = [
  { id: "1", name: "P.301", building: "Tòa A", capacity: 40, latitude: 21.028511, longitude: 105.804817 },
  { id: "2", name: "P.302", building: "Tòa A", capacity: 50, latitude: 21.028522, longitude: 105.804825 },
  { id: "3", name: "P.401", building: "Tòa B", capacity: 100, latitude: null, longitude: null },
  { id: "4", name: "Lab 1", building: "Tòa C", capacity: 30, latitude: 21.028600, longitude: 105.804900 },
];

export default function RoomsPage() {
  const [rooms, setRooms] = useState<Room[]>(mockRooms);
  const [search, setSearch] = useState("");
  const [showAddModal, setShowAddModal] = useState(false);
  const [form, setForm] = useState({ name: "", building: "", capacity: "", latitude: "", longitude: "" });
  const [isGettingLocation, setIsGettingLocation] = useState(false);
  
  const [showEditModal, setShowEditModal] = useState(false);
  const [editTarget, setEditTarget] = useState<Room | null>(null);

  const handleGetLocation = () => {
    if ("geolocation" in navigator) {
      setIsGettingLocation(true);
      navigator.geolocation.getCurrentPosition(
        (position) => {
          setForm({
            ...form,
            latitude: position.coords.latitude.toString(),
            longitude: position.coords.longitude.toString(),
          });
          setIsGettingLocation(false);
        },
        (error) => {
          console.error("Lỗi lấy tọa độ:", error);
          setIsGettingLocation(false);
          alert("Không thể lấy tọa độ. Vui lòng cho phép quyền truy cập vị trí.");
        }
      );
    } else {
      alert("Trình duyệt của bạn không hỗ trợ lấy tọa độ.");
    }
  };

  const handleAdd = (e: React.FormEvent) => {
    e.preventDefault();
    setRooms([...rooms, {
      id: Math.random().toString(),
      name: form.name,
      building: form.building,
      capacity: parseInt(form.capacity) || 0,
      latitude: parseFloat(form.latitude) || null,
      longitude: parseFloat(form.longitude) || null,
    }]);
    setShowAddModal(false);
    setForm({ name: "", building: "", capacity: "", latitude: "", longitude: "" });
  };

  const openEditModal = (room: Room) => {
    setEditTarget(room);
    setForm({ 
      name: room.name, 
      building: room.building, 
      capacity: room.capacity.toString(), 
      latitude: room.latitude ? room.latitude.toString() : "", 
      longitude: room.longitude ? room.longitude.toString() : "" 
    });
    setShowEditModal(true);
  };

  const handleEdit = (e: React.FormEvent) => {
    e.preventDefault();
    if (!editTarget) return;
    setRooms(rooms.map(r => r.id === editTarget.id ? {
      ...r,
      name: form.name,
      building: form.building,
      capacity: parseInt(form.capacity) || 0,
      latitude: parseFloat(form.latitude) || null,
      longitude: parseFloat(form.longitude) || null,
    } : r));
    setShowEditModal(false);
    setEditTarget(null);
    setForm({ name: "", building: "", capacity: "", latitude: "", longitude: "" });
  };

  const filteredRooms = rooms.filter(r => 
    r.name.toLowerCase().includes(search.toLowerCase()) || 
    r.building.toLowerCase().includes(search.toLowerCase())
  );

  return (
    <main className="flex-1 overflow-y-auto mt-16 p-xl bg-background">
      {/* Header */}
      <div className="flex justify-between items-end mb-lg">
        <div>
          <h2 className="text-h1 text-on-surface">Phòng học</h2>
          <p className="text-body-md text-on-surface-variant mt-1">
            Quản lý cơ sở vật chất và tọa độ định vị (Geo-fencing)
          </p>
        </div>
        <div className="flex gap-2">
          <button 
            onClick={() => setShowAddModal(true)}
            className="bg-primary hover:bg-surface-tint text-on-primary text-button px-4 py-2 rounded-lg flex items-center gap-2 shadow-sm transition-colors duration-200"
          >
            <span className="material-symbols-outlined text-[20px]">add</span>
            Thêm Phòng học
          </button>
        </div>
      </div>

      {/* Data Card */}
      <div className="bg-surface-container-lowest rounded-xl shadow-[var(--shadow-card)] border border-border-muted overflow-hidden flex flex-col">
        {/* Toolbar */}
        <div className="p-md border-b border-border-muted flex gap-3 items-center">
          <div className="relative w-full sm:w-96">
            <span className="material-symbols-outlined absolute left-3 top-1/2 -translate-y-1/2 text-on-surface-variant text-[20px]">search</span>
            <input
              className="w-full pl-10 pr-4 py-2 bg-surface rounded-lg border border-outline-variant focus:border-primary focus:ring-1 focus:ring-primary outline-none transition-all text-body-sm text-on-surface placeholder:text-on-surface-variant/70"
              placeholder="Tìm kiếm phòng học, tòa nhà..."
              type="text"
              value={search}
              onChange={(e) => setSearch(e.target.value)}
            />
          </div>
        </div>

        {/* Table */}
        <div className="flex-1 overflow-auto">
          <table className="w-full text-left border-collapse">
            <thead className="bg-surface sticky top-0 z-10 border-b border-border-muted">
              <tr>
                <th className="py-3 px-4 text-label text-on-surface-variant uppercase tracking-wider">Tên Phòng</th>
                <th className="py-3 px-4 text-label text-on-surface-variant uppercase tracking-wider">Tòa nhà</th>
                <th className="py-3 px-4 text-label text-on-surface-variant uppercase tracking-wider">Sức chứa</th>
                <th className="py-3 px-4 text-label text-on-surface-variant uppercase tracking-wider">Tọa độ GPS (Geo-fencing)</th>
                <th className="py-3 px-4 text-label text-on-surface-variant uppercase tracking-wider text-right">Thao tác</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-outline-variant/20 text-body-sm text-on-surface">
              {filteredRooms.map(room => (
                <tr key={room.id} className="hover:bg-surface-container-lowest transition-colors">
                  <td className="py-3 px-4 font-semibold text-on-surface">{room.name}</td>
                  <td className="py-3 px-4">{room.building}</td>
                  <td className="py-3 px-4 text-on-surface-variant">{room.capacity} chỗ</td>
                  <td className="py-3 px-4">
                    {room.latitude && room.longitude ? (
                      <div className="flex items-center gap-2 text-primary bg-primary-container/20 px-2 py-1 rounded-md w-max">
                        <span className="material-symbols-outlined text-[16px]">location_on</span>
                        <span className="font-mono text-xs">{room.latitude}, {room.longitude}</span>
                      </div>
                    ) : (
                      <span className="text-error flex items-center gap-1 text-xs bg-error-container/20 px-2 py-1 rounded-md w-max">
                        <span className="material-symbols-outlined text-[14px]">location_off</span>
                        Chưa thiết lập
                      </span>
                    )}
                  </td>
                  <td className="py-3 px-4 text-right">
                    <div className="flex justify-end gap-2">
                      <button onClick={() => openEditModal(room)} className="p-1.5 text-on-surface-variant hover:text-primary hover:bg-primary-container/20 rounded-md transition-colors" title="Chỉnh sửa">
                        <span className="material-symbols-outlined text-[18px]">edit</span>
                      </button>
                      <button className="p-1.5 text-on-surface-variant hover:text-error hover:bg-error-container/20 rounded-md transition-colors" title="Xóa">
                        <span className="material-symbols-outlined text-[18px]">delete</span>
                      </button>
                    </div>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      </div>

      {/* Add Room Modal */}
      {showAddModal && (
        <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/40 backdrop-blur-sm p-4">
          <div className="bg-surface-container-lowest rounded-xl shadow-[var(--shadow-float)] border border-border-muted p-xl w-full max-w-3xl">
            <h3 className="text-h3 text-on-surface mb-md">Thêm Phòng học mới</h3>
            <form className="space-y-4" onSubmit={handleAdd}>
              <div className="grid grid-cols-2 gap-4">
                <div>
                  <label className="block text-label font-label text-on-surface-variant mb-1">Tên phòng <span className="text-error">*</span></label>
                  <input type="text" value={form.name} onChange={e => setForm({...form, name: e.target.value})} className="w-full bg-surface border border-outline-variant rounded-lg px-3 py-2 text-body-md focus:border-primary outline-none" placeholder="VD: P.301" required />
                </div>
                <div>
                  <label className="block text-label font-label text-on-surface-variant mb-1">Tòa nhà <span className="text-error">*</span></label>
                  <input type="text" value={form.building} onChange={e => setForm({...form, building: e.target.value})} className="w-full bg-surface border border-outline-variant rounded-lg px-3 py-2 text-body-md focus:border-primary outline-none" placeholder="VD: Tòa A" required />
                </div>
              </div>
              <div>
                <label className="block text-label font-label text-on-surface-variant mb-1">Sức chứa (người)</label>
                <input type="number" value={form.capacity} onChange={e => setForm({...form, capacity: e.target.value})} className="w-full bg-surface border border-outline-variant rounded-lg px-3 py-2 text-body-md focus:border-primary outline-none" placeholder="VD: 50" />
              </div>
              <div className="p-4 bg-surface-container-low rounded-lg border border-outline-variant/50">
                <div className="flex justify-between items-center mb-3">
                  <h4 className="text-body-sm font-semibold flex items-center gap-2">
                    <span className="material-symbols-outlined text-[18px]">share_location</span>
                    Tọa độ định vị (Geo-fencing)
                  </h4>
                  <button 
                    type="button" 
                    onClick={handleGetLocation}
                    disabled={isGettingLocation}
                    className="flex items-center gap-1.5 text-xs font-semibold text-primary hover:bg-primary-container/30 px-2 py-1 rounded transition-colors disabled:opacity-50"
                  >
                    <span className="material-symbols-outlined text-[16px]">{isGettingLocation ? 'hourglass_empty' : 'my_location'}</span>
                    {isGettingLocation ? 'Đang lấy...' : 'Lấy vị trí hiện tại'}
                  </button>
                </div>
                <div className="grid grid-cols-2 gap-4">
                  <div>
                    <label className="block text-label font-label text-on-surface-variant mb-1">Vĩ độ (Latitude)</label>
                    <input type="number" step="any" value={form.latitude} onChange={e => setForm({...form, latitude: e.target.value})} className="w-full bg-surface border border-outline-variant rounded-lg px-3 py-2 text-body-md focus:border-primary outline-none" placeholder="21.028511" />
                  </div>
                  <div>
                    <label className="block text-label font-label text-on-surface-variant mb-1">Kinh độ (Longitude)</label>
                    <input type="number" step="any" value={form.longitude} onChange={e => setForm({...form, longitude: e.target.value})} className="w-full bg-surface border border-outline-variant rounded-lg px-3 py-2 text-body-md focus:border-primary outline-none" placeholder="105.804817" />
                  </div>
                </div>
                <p className="text-xs text-on-surface-variant mt-2">Dùng để xác thực vị trí sinh viên khi điểm danh bằng mã QR.</p>
              </div>
              <div className="flex gap-3 justify-end pt-4 border-t border-outline-variant/30">
                <button type="button" onClick={() => setShowAddModal(false)} className="px-4 py-2 rounded-lg border border-outline-variant hover:bg-surface-container transition-colors">Hủy</button>
                <button type="submit" className="px-4 py-2 rounded-lg bg-primary text-on-primary hover:bg-primary/90 transition-colors">Lưu phòng học</button>
              </div>
            </form>
          </div>
        </div>
      )}

      {/* Edit Room Modal */}
      {showEditModal && (
        <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/40 backdrop-blur-sm p-4">
          <div className="bg-surface-container-lowest rounded-xl shadow-[var(--shadow-float)] border border-border-muted p-xl w-full max-w-3xl">
            <h3 className="text-h3 text-on-surface mb-md">Cập nhật Phòng học</h3>
            <form className="space-y-4" onSubmit={handleEdit}>
              <div className="grid grid-cols-2 gap-4">
                <div>
                  <label className="block text-label font-label text-on-surface-variant mb-1">Tên phòng <span className="text-error">*</span></label>
                  <input type="text" value={form.name} onChange={e => setForm({...form, name: e.target.value})} className="w-full bg-surface border border-outline-variant rounded-lg px-3 py-2 text-body-md focus:border-primary outline-none" required />
                </div>
                <div>
                  <label className="block text-label font-label text-on-surface-variant mb-1">Tòa nhà <span className="text-error">*</span></label>
                  <input type="text" value={form.building} onChange={e => setForm({...form, building: e.target.value})} className="w-full bg-surface border border-outline-variant rounded-lg px-3 py-2 text-body-md focus:border-primary outline-none" required />
                </div>
              </div>
              <div>
                <label className="block text-label font-label text-on-surface-variant mb-1">Sức chứa (người)</label>
                <input type="number" value={form.capacity} onChange={e => setForm({...form, capacity: e.target.value})} className="w-full bg-surface border border-outline-variant rounded-lg px-3 py-2 text-body-md focus:border-primary outline-none" />
              </div>
              <div className="p-4 bg-surface-container-low rounded-lg border border-outline-variant/50">
                <div className="flex justify-between items-center mb-3">
                  <h4 className="text-body-sm font-semibold flex items-center gap-2">
                    <span className="material-symbols-outlined text-[18px]">share_location</span>
                    Tọa độ định vị (Geo-fencing)
                  </h4>
                  <button 
                    type="button" 
                    onClick={handleGetLocation}
                    disabled={isGettingLocation}
                    className="flex items-center gap-1.5 text-xs font-semibold text-primary hover:bg-primary-container/30 px-2 py-1 rounded transition-colors disabled:opacity-50"
                  >
                    <span className="material-symbols-outlined text-[16px]">{isGettingLocation ? 'hourglass_empty' : 'my_location'}</span>
                    {isGettingLocation ? 'Đang lấy...' : 'Lấy vị trí hiện tại'}
                  </button>
                </div>
                <div className="grid grid-cols-2 gap-4">
                  <div>
                    <label className="block text-label font-label text-on-surface-variant mb-1">Vĩ độ (Latitude)</label>
                    <input type="number" step="any" value={form.latitude} onChange={e => setForm({...form, latitude: e.target.value})} className="w-full bg-surface border border-outline-variant rounded-lg px-3 py-2 text-body-md focus:border-primary outline-none" />
                  </div>
                  <div>
                    <label className="block text-label font-label text-on-surface-variant mb-1">Kinh độ (Longitude)</label>
                    <input type="number" step="any" value={form.longitude} onChange={e => setForm({...form, longitude: e.target.value})} className="w-full bg-surface border border-outline-variant rounded-lg px-3 py-2 text-body-md focus:border-primary outline-none" />
                  </div>
                </div>
              </div>
              <div className="flex gap-3 justify-end pt-4 border-t border-outline-variant/30">
                <button type="button" onClick={() => setShowEditModal(false)} className="px-4 py-2 rounded-lg border border-outline-variant hover:bg-surface-container transition-colors">Hủy</button>
                <button type="submit" className="px-4 py-2 rounded-lg bg-primary text-on-primary hover:bg-primary/90 transition-colors">Lưu thay đổi</button>
              </div>
            </form>
          </div>
        </div>
      )}
    </main>
  );
}
