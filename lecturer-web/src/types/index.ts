// ============================================================
// UniVerse — Shared TypeScript Interfaces (fe/web)
// Đồng bộ với Backend (NestJS + TypeORM + Mongoose)
// ============================================================

// ── Auth ─────────────────────────────────────────────────────
export type UserRole = "student" | "lecturer" | "admin";

export interface AuthResponse {
  accessToken: string;
  refreshToken: string;
  user: User;
}

export interface RefreshTokenResponse {
  accessToken: string;
}

// ── User ─────────────────────────────────────────────────────
export interface User {
  id: string;
  code: string;          // MSSV / MGV
  name: string;
  email: string;
  role: UserRole;
  department?: string;
  avatarUrl?: string;
  status: "active" | "suspended" | "graduated";
  createdAt: string;
  updatedAt: string;
}

export interface CreateUserDto {
  code: string;
  name: string;
  email: string;
  password: string;
  role: UserRole;
  department?: string;
}

export interface UpdateUserDto extends Partial<CreateUserDto> {}

// ── Course ───────────────────────────────────────────────────
export interface Course {
  id: string;
  code: string;
  name: string;
  credits: number;
  department: string;
  description?: string;
}

// ── Class ────────────────────────────────────────────────────
export interface Class {
  id: string;
  code: string;
  courseId: string;
  course?: Course;
  lecturerId: string;
  lecturer?: User;
  semester: string;
  roomId?: string;
  room?: Room;
  studentCount: number;
  enrollments?: Enrollment[];
  attendanceRate?: number;
}

// ── Room ─────────────────────────────────────────────────────
export interface Room {
  id: string;
  name: string;
  building: string;
  capacity: number;
  latitude: number;
  longitude: number;
}

// ── Schedule ─────────────────────────────────────────────────
export interface Schedule {
  id: string;
  classId: string;
  class?: Class;
  roomId: string;
  room?: Room;
  dayOfWeek: number;    // 1=Mon ... 7=Sun
  startPeriod: number;
  endPeriod: number;
  startDate: string;
  endDate: string;
}

// ── Attendance ───────────────────────────────────────────────
export type AttendanceStatus = "present" | "absent" | "excused" | "late";

export interface Attendance {
  id: string;
  scheduleId: string;
  studentId: string;
  student?: User;
  status: AttendanceStatus;
  note?: string;
  checkedAt?: string;
  sessionDate: string;
}

export interface AttendanceSession {
  id: string;
  classId: string;
  scheduleId: string;
  qrToken?: string;
  qrExpiresAt?: string;
  isActive: boolean;
  sessionDate: string;
  attendances: Attendance[];
}

// ── Grade ────────────────────────────────────────────────────
export interface Grade {
  id: string;
  classId: string;
  studentId: string;
  student?: User;
  qtScore: number | null;   // Quá trình (20%)
  gkScore: number | null;   // Giữa kỳ (30%)
  ckScore: number | null;   // Cuối kỳ (50%)
  totalScore?: number | null;
  isPublished: boolean;
  publishedAt?: string;
}

// ── Enrollment ───────────────────────────────────────────────
export interface Enrollment {
  id: string;
  studentId: string;
  student?: User;
  classId: string;
  enrolledAt: string;
}

// ── Message / Chat ───────────────────────────────────────────
export interface Message {
  id: string;
  conversationId: string;
  senderId: string;
  sender?: User;
  content: string;
  type: "text" | "image" | "file";
  createdAt: string;
  readAt?: string;
}

export interface Conversation {
  id: string;
  studentId: string;
  student?: User;
  lecturerId: string;
  lecturer?: User;
  lastMessage?: Message;
  unreadCount: number;
  updatedAt: string;
}

// ── Notification ─────────────────────────────────────────────
export interface Notification {
  id: string;
  title: string;
  content: string;
  type: "system" | "class" | "grade" | "attendance";
  targetType: "all" | "department" | "class" | "user";
  targetId?: string;
  senderId?: string;
  sender?: User;
  isRead?: boolean;
  createdAt: string;
}

export interface SendNotificationDto {
  title: string;
  content: string;
  type: Notification["type"];
  targetType: Notification["targetType"];
  targetId?: string;
}

// ── Leave Request ────────────────────────────────────────────
export type LeaveRequestStatus = "pending" | "approved" | "rejected";

export interface LeaveRequest {
  id: string;
  studentId: string;
  student?: User;
  classId: string;
  class?: Class;
  date: string;
  reason: string;
  evidenceUrl?: string;
  status: LeaveRequestStatus;
  reviewNote?: string;
  reviewedAt?: string;
  createdAt: string;
}

// ── Pagination ───────────────────────────────────────────────
export interface PaginatedResponse<T> {
  data: T[];
  total: number;
  page: number;
  limit: number;
  totalPages: number;
}

export interface PaginationParams {
  page?: number;
  limit?: number;
  search?: string;
  sortBy?: string;
  sortOrder?: "asc" | "desc";
}

// ── Statistics ───────────────────────────────────────────────
export interface AttendanceStats {
  classId: string;
  className: string;
  totalSessions: number;
  attendanceRate: number;
  presentCount: number;
  absentCount: number;
}

export interface SystemStats {
  totalStudents: number;
  totalLecturers: number;
  activeClasses: number;
  overallAttendanceRate: number;
  byDepartment: { department: string; rate: number }[];
}

// ── API Error ────────────────────────────────────────────────
export interface ApiError {
  statusCode: number;
  message: string | string[];
  error?: string;
}
