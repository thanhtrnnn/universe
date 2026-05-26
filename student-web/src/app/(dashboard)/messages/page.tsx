"use client";

import React, { useState, useEffect, useRef } from "react";

interface Message {
  id: string;
  senderId: string;
  senderName: string;
  content: string;
  timestamp: string;
  isMine: boolean;
}

interface Conversation {
  id: string;
  name: string;
  type: "group" | "private";
  lastMessage: string;
  unreadCount: number;
  isOnline?: boolean;
}

const mockConversations: Conversation[] = [
  { id: "c1", name: "Lớp Nhập môn CNPM", type: "group", lastMessage: "Chào cả lớp, tuần này học online nhé.", unreadCount: 0 },
  { id: "c2", name: "Lớp Lập trình Web", type: "group", lastMessage: "Có ai làm xong bài tập chưa?", unreadCount: 5 },
  { id: "p1", name: "Đỗ Thị Liên (Giảng viên)", type: "private", lastMessage: "Cô cho em hỏi phần này...", unreadCount: 1, isOnline: true },
  { id: "p2", name: "VP1C", type: "private", lastMessage: "Lịch học bù sẽ xếp vào thứ 7 nhé.", unreadCount: 0, isOnline: true },
];

const mockMessages: Record<string, Message[]> = {
  "c1": [
    { id: "m1", senderId: "t1", senderName: "Đỗ Thị Liên", content: "Chào cả lớp, tuần này học online nhé.", timestamp: "08:00 AM", isMine: false },
    { id: "m2", senderId: "s1", senderName: "Sinh viên 1", content: "Dạ vâng ạ.", timestamp: "08:05 AM", isMine: false },
    { id: "m3", senderId: "s2", senderName: "Phạm Thị Thiên Hà", content: "Dùng link Google Meet cũ đúng không cô?", timestamp: "08:10 AM", isMine: true },
  ],
  "p1": [
    { id: "m4", senderId: "s1", senderName: "Phạm Thị Thiên Hà", content: "Cô cho em hỏi phần này...", timestamp: "Hôm qua", isMine: true },
  ]
};

export default function StudentChatPage() {
  const [conversations, setConversations] = useState<Conversation[]>(mockConversations);
  const [activeConvId, setActiveConvId] = useState<string>(mockConversations[0].id);
  const [messages, setMessages] = useState<Message[]>(mockMessages[mockConversations[0].id] || []);
  const [inputText, setInputText] = useState("");
  const [showMenu, setShowMenu] = useState(false);
  const messagesEndRef = useRef<HTMLDivElement>(null);
  const menuRef = useRef<HTMLDivElement>(null);

  useEffect(() => {
    // Mock Socket.IO connection setup
    const timer = setInterval(() => {
       setConversations(prev => prev.map(c => c.type === 'private' ? { ...c, isOnline: Math.random() > 0.5 } : c));
    }, 10000);
    return () => clearInterval(timer);
  }, []);

  useEffect(() => {
    setMessages(mockMessages[activeConvId] || []);
    // Clear unread
    setConversations(prev => prev.map(c => c.id === activeConvId ? { ...c, unreadCount: 0 } : c));
    setShowMenu(false);
    scrollToBottom();
  }, [activeConvId]);

  useEffect(() => {
    const handleClickOutside = (e: MouseEvent) => {
      if (menuRef.current && !menuRef.current.contains(e.target as Node)) {
        setShowMenu(false);
      }
    };
    document.addEventListener("mousedown", handleClickOutside);
    return () => document.removeEventListener("mousedown", handleClickOutside);
  }, []);

  const scrollToBottom = () => {
    messagesEndRef.current?.scrollIntoView({ behavior: "smooth" });
  };

  const handleSend = (e: React.FormEvent) => {
    e.preventDefault();
    if (!inputText.trim()) return;
    
    const newMsg: Message = {
      id: Math.random().toString(),
      senderId: "me",
      senderName: "Phạm Thị Thiên Hà",
      content: inputText,
      timestamp: new Date().toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' }),
      isMine: true
    };
    
    setMessages(prev => [...prev, newMsg]);
    setConversations(prev => prev.map(c => c.id === activeConvId ? { ...c, lastMessage: inputText } : c));
    setInputText("");
    setTimeout(scrollToBottom, 100);
  };

  const activeConv = conversations.find(c => c.id === activeConvId);

  return (
    <main className="flex-1 overflow-hidden mt-16 p-xl bg-background flex flex-col h-[calc(100vh-64px)]">
      <div className="flex justify-between items-end mb-lg shrink-0">
        <div>
          <h2 className="text-h1 text-on-surface">Tin nhắn</h2>
          <p className="text-body-md text-on-surface-variant mt-1">
            Giao tiếp với giảng viên, bạn học và phòng đào tạo
          </p>
        </div>
      </div>

      <div className="flex-1 flex bg-surface-container-lowest rounded-xl shadow-[var(--shadow-card)] border border-border-muted overflow-hidden h-full">
        {/* Sidebar */}
        <div className="w-80 border-r border-border-muted flex flex-col h-full bg-surface">
          <div className="p-md border-b border-border-muted">
            <div className="relative">
              <span className="material-symbols-outlined absolute left-3 top-1/2 -translate-y-1/2 text-on-surface-variant text-[20px]">search</span>
              <input
                className="w-full pl-10 pr-4 py-2 bg-background rounded-lg border border-outline-variant focus:border-primary focus:ring-1 focus:ring-primary outline-none transition-all text-body-sm"
                placeholder="Tìm hội thoại..."
                type="text"
              />
            </div>
          </div>
          <div className="flex-1 overflow-y-auto custom-scrollbar">
            {conversations.map(conv => (
              <button
                key={conv.id}
                onClick={() => setActiveConvId(conv.id)}
                className={`w-full p-4 flex items-center gap-3 text-left transition-colors border-l-4 ${activeConvId === conv.id ? 'bg-primary-container/20 border-primary' : 'border-transparent hover:bg-surface-container-low'}`}
              >
                <div className="relative">
                  <div className={`w-12 h-12 rounded-full flex items-center justify-center text-white text-body-lg font-semibold ${conv.type === 'group' ? 'bg-tertiary' : 'bg-primary'}`}>
                    {conv.type === 'group' ? <span className="material-symbols-outlined">groups</span> : conv.name.charAt(0)}
                  </div>
                  {conv.type === 'private' && (
                    <div className={`absolute bottom-0 right-0 w-3.5 h-3.5 rounded-full border-2 border-surface ${conv.isOnline ? 'bg-green-500' : 'bg-outline-variant'}`}></div>
                  )}
                </div>
                <div className="flex-1 min-w-0">
                  <div className="flex justify-between items-center mb-1">
                    <h4 className="text-body-md font-semibold text-on-surface truncate">{conv.name}</h4>
                    {conv.unreadCount > 0 && (
                      <span className="bg-error text-on-error text-[10px] font-bold px-1.5 py-0.5 rounded-full">{conv.unreadCount}</span>
                    )}
                  </div>
                  <p className={`text-body-sm truncate ${conv.unreadCount > 0 ? 'text-on-surface font-semibold' : 'text-on-surface-variant'}`}>
                    {conv.lastMessage}
                  </p>
                </div>
              </button>
            ))}
          </div>
        </div>

        {/* Chat Area */}
        {activeConv ? (
          <div className="flex-1 flex flex-col h-full bg-background relative">
            <div className="p-4 border-b border-border-muted bg-surface flex justify-between items-center z-10 shadow-sm">
              <div className="flex items-center gap-3">
                <div className={`w-10 h-10 rounded-full flex items-center justify-center text-white ${activeConv.type === 'group' ? 'bg-tertiary' : 'bg-primary'}`}>
                   {activeConv.type === 'group' ? <span className="material-symbols-outlined">groups</span> : activeConv.name.charAt(0)}
                </div>
                <div>
                  <h3 className="text-body-lg font-semibold text-on-surface">{activeConv.name}</h3>
                  {activeConv.type === 'private' ? (
                    <p className={`text-xs ${activeConv.isOnline ? 'text-green-600' : 'text-on-surface-variant'}`}>{activeConv.isOnline ? 'Đang hoạt động' : 'Ngoại tuyến'}</p>
                  ) : (
                    <p className="text-xs text-on-surface-variant">Nhóm chat lớp</p>
                  )}
                </div>
              </div>
              <div className="relative" ref={menuRef}>
                <button onClick={() => setShowMenu(!showMenu)} className="p-2 rounded-full hover:bg-surface-container transition-colors text-on-surface-variant focus:bg-surface-container">
                  <span className="material-symbols-outlined">more_vert</span>
                </button>
                
                {showMenu && (
                  <div className="absolute right-0 top-full mt-2 w-56 bg-surface-container-lowest border border-border-muted rounded-xl shadow-[var(--shadow-float)] overflow-hidden z-50">
                    <div className="py-2">
                      {activeConv.type === 'group' ? (
                        <>
                          <button onClick={() => setShowMenu(false)} className="w-full px-4 py-2 text-left text-body-sm text-on-surface hover:bg-surface-container transition-colors flex items-center gap-3">
                            <span className="material-symbols-outlined text-[18px]">group</span>
                            Xem danh sách thành viên
                          </button>
                          <div className="h-px bg-border-muted my-1 w-full" />
                          <button onClick={() => setShowMenu(false)} className="w-full px-4 py-2 text-left text-body-sm text-on-surface hover:bg-surface-container transition-colors flex items-center gap-3">
                            <span className="material-symbols-outlined text-[18px]">notifications_off</span>
                            Tắt thông báo
                          </button>
                        </>
                      ) : (
                        <>
                          <button onClick={() => setShowMenu(false)} className="w-full px-4 py-2 text-left text-body-sm text-on-surface hover:bg-surface-container transition-colors flex items-center gap-3">
                            <span className="material-symbols-outlined text-[18px]">person</span>
                            Xem hồ sơ giảng viên
                          </button>
                          <div className="h-px bg-border-muted my-1 w-full" />
                          <button onClick={() => setShowMenu(false)} className="w-full px-4 py-2 text-left text-body-sm text-error hover:bg-error-container/20 transition-colors flex items-center gap-3">
                            <span className="material-symbols-outlined text-[18px]">delete</span>
                            Xóa cuộc trò chuyện
                          </button>
                        </>
                      )}
                    </div>
                  </div>
                )}
              </div>
            </div>

            <div className="flex-1 p-6 overflow-y-auto custom-scrollbar flex flex-col gap-4">
              {messages.length === 0 ? (
                <div className="m-auto text-center text-on-surface-variant">
                  <span className="material-symbols-outlined text-[48px] mb-2 opacity-20">chat_bubble</span>
                  <p>Bắt đầu cuộc trò chuyện mới</p>
                </div>
              ) : (
                messages.map(msg => (
                  <div key={msg.id} className={`flex max-w-[70%] ${msg.isMine ? 'ml-auto justify-end' : ''}`}>
                    {!msg.isMine && activeConv.type === 'group' && (
                      <div className="w-8 h-8 rounded-full bg-secondary text-on-secondary flex items-center justify-center text-xs font-bold mr-2 shrink-0">
                        {msg.senderName.charAt(0)}
                      </div>
                    )}
                    <div>
                      {!msg.isMine && activeConv.type === 'group' && (
                        <div className="text-xs text-on-surface-variant mb-1 ml-1">{msg.senderName}</div>
                      )}
                      <div className={`p-3 rounded-2xl ${msg.isMine ? 'bg-primary text-on-primary rounded-tr-sm' : 'bg-surface border border-outline-variant/30 text-on-surface rounded-tl-sm shadow-sm'}`}>
                        <p className="text-body-md whitespace-pre-wrap leading-relaxed">{msg.content}</p>
                      </div>
                      <div className={`text-[11px] text-on-surface-variant mt-1 ${msg.isMine ? 'text-right mr-1' : 'ml-1'}`}>
                        {msg.timestamp}
                      </div>
                    </div>
                  </div>
                ))
              )}
              <div ref={messagesEndRef} />
            </div>

            <div className="p-4 bg-surface border-t border-border-muted z-10">
              <form onSubmit={handleSend} className="flex gap-2 items-center bg-background border border-outline-variant rounded-full pr-2 pl-4 py-1 focus-within:border-primary focus-within:ring-1 focus-within:ring-primary transition-all">
                <input
                  type="text"
                  value={inputText}
                  onChange={e => setInputText(e.target.value)}
                  placeholder="Nhập tin nhắn..."
                  className="flex-1 bg-transparent border-none outline-none text-body-md py-2"
                />
                <button type="button" className="p-2 text-on-surface-variant hover:text-primary transition-colors">
                  <span className="material-symbols-outlined text-[20px]">attach_file</span>
                </button>
                <button type="submit" disabled={!inputText.trim()} className="w-10 h-10 rounded-full bg-primary text-on-primary flex items-center justify-center disabled:opacity-50 disabled:bg-surface-container-high disabled:text-on-surface-variant transition-colors shadow-sm">
                  <span className="material-symbols-outlined text-[20px]">send</span>
                </button>
              </form>
            </div>
          </div>
        ) : (
          <div className="flex-1 flex flex-col items-center justify-center text-on-surface-variant bg-background">
            <span className="material-symbols-outlined text-[64px] mb-4 opacity-20">forum</span>
            <p className="text-body-lg">Chọn một hội thoại để bắt đầu nhắn tin</p>
          </div>
        )}
      </div>
    </main>
  );
}
