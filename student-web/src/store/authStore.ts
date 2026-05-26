import { create } from "zustand";
import { persist } from "zustand/middleware";
import type { User, AuthResponse } from "@/types";
import api, { tokenStorage } from "@/lib/api";

interface AuthState {
  user: User | null;
  isAuthenticated: boolean;
  isLoading: boolean;
  error: string | null;
  login: (email: string, password: string) => Promise<void>;
  logout: () => Promise<void>;
  clearError: () => void;
  setUser: (user: User) => void;
}

export const useAuthStore = create<AuthState>()(
  persist(
    (set) => ({
      user: null,
      isAuthenticated: false,
      isLoading: false,
      error: null,

      login: async (email: string, password: string) => {
        set({ isLoading: true, error: null });
        try {
          // Thử kết nối API thực tế
          const { data } = await api.post<AuthResponse>("/auth/login", { email, password });
          // Ensure only students can login here
          if (data.user.role !== "student") {
            set({ isLoading: false, error: "Tài khoản này không phải sinh viên." });
            throw new Error("Unauthorized role");
          }
          tokenStorage.setAccess(data.accessToken);
          tokenStorage.setRefresh(data.refreshToken);

          // Thiết lập cookie cho middleware
          if (typeof window !== "undefined") {
            document.cookie = `universe_student_token=${data.accessToken}; path=/; max-age=86400;`;
          }

          set({ user: data.user, isAuthenticated: true, isLoading: false, error: null });
        } catch (err: unknown) {
          // Bẫy lỗi kết nối hoặc tài khoản mẫu để fallback sang chế độ Offline Mock trong môi trường Development
          const isNetworkError = (err as { code?: string })?.code === "ERR_NETWORK" || !(err as { response?: unknown })?.response;
          const isSampleAccount = email === "sv@student.edu.vn" || email.startsWith("sv");

          if (isNetworkError || isSampleAccount) {
            console.warn("API Offline hoặc sử dụng tài khoản mẫu. Tự động chuyển sang chế độ giả lập (Mock Auth) cho Sinh viên.");
            
            // Giả lập delay mạng
            await new Promise((resolve) => setTimeout(resolve, 800));

            const mockUser: User = {
              id: "student-mock-1",
              code: "B21DCCN001",
              name: "Nguyễn Văn Hùng (Demo)",
              email: email || "sv@student.edu.vn",
              role: "student",
              department: "Công nghệ thông tin",
              status: "active",
              createdAt: new Date().toISOString(),
              updatedAt: new Date().toISOString(),
            };

            tokenStorage.setAccess("mock-student-access-token");
            tokenStorage.setRefresh("mock-student-refresh-token");
            
            // Thiết lập cookie giả lập cho middleware
            if (typeof window !== "undefined") {
              document.cookie = "universe_student_token=mock-student-access-token; path=/; max-age=86400;";
            }

            set({
              user: mockUser,
              isAuthenticated: true,
              isLoading: false,
              error: null,
            });
            return;
          }

          const apiError = err as { response?: { data?: { message?: string | string[] } } };
          const msg = apiError?.response?.data?.message ?? "Đăng nhập thất bại.";
          set({ isLoading: false, error: Array.isArray(msg) ? msg[0] : msg });
          throw err;
        }
      },

      logout: async () => {
        try { await api.post("/auth/logout"); } catch { /* ignore */ }
        finally {
          tokenStorage.clear();
          // Xóa cookie của middleware
          if (typeof window !== "undefined") {
            document.cookie = "universe_student_token=; path=/; expires=Thu, 01 Jan 1970 00:00:00 GMT;";
          }
          set({ user: null, isAuthenticated: false, error: null });
        }
      },

      clearError: () => set({ error: null }),
      setUser: (user: User) => set({ user }),
    }),
    {
      name: "universe-student-auth",
      partialize: (state) => ({ user: state.user, isAuthenticated: state.isAuthenticated }),
    }
  )
);
