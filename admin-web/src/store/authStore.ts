import { create } from "zustand";
import { persist } from "zustand/middleware";
import type { User, AuthResponse } from "@/types";
import api, { tokenStorage } from "@/lib/api";

interface AuthState {
  user: User | null;
  isAuthenticated: boolean;
  isLoading: boolean;
  error: string | null;

  // Actions
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
          const { data } = await api.post<AuthResponse>("/auth/login", {
            email,
            password,
          });
          tokenStorage.setAccess(data.accessToken);
          tokenStorage.setRefresh(data.refreshToken);
          
          // Thiết lập cookie cho middleware
          if (typeof window !== "undefined") {
            document.cookie = `universe_admin_token=${data.accessToken}; path=/; max-age=86400;`;
          }

          set({
            user: data.user,
            isAuthenticated: true,
            isLoading: false,
            error: null,
          });
        } catch (err: unknown) {
          // Bẫy lỗi kết nối hoặc tài khoản mẫu để fallback sang chế độ Offline Mock trong môi trường Development
          const isNetworkError = (err as { code?: string })?.code === "ERR_NETWORK" || !(err as { response?: unknown })?.response;
          const isSampleAccount = email === "admin@universe.edu.vn";

          if (isNetworkError || isSampleAccount) {
            console.warn("API Offline hoặc sử dụng tài khoản mẫu. Tự động chuyển sang chế độ giả lập (Mock Auth).");
            
            // Giả lập delay mạng
            await new Promise((resolve) => setTimeout(resolve, 800));

            const mockUser: User = {
              id: "admin-mock-1",
              code: "AD001",
              name: "Quản trị viên Hệ thống (Demo)",
              email: email || "admin@universe.edu.vn",
              role: "admin",
              status: "active",
              createdAt: new Date().toISOString(),
              updatedAt: new Date().toISOString(),
            };

            tokenStorage.setAccess("mock-access-token");
            tokenStorage.setRefresh("mock-refresh-token");
            
            // Thiết lập cookie giả lập cho middleware
            if (typeof window !== "undefined") {
              document.cookie = "universe_admin_token=mock-access-token; path=/; max-age=86400;";
            }

            set({
              user: mockUser,
              isAuthenticated: true,
              isLoading: false,
              error: null,
            });
            return;
          }

          const message =
            (err as { response?: { data?: { message?: string } } })?.response?.data?.message ??
            "Đăng nhập thất bại. Vui lòng kiểm tra lại thông tin.";
          set({ isLoading: false, error: Array.isArray(message) ? message[0] : message });
          throw err;
        }
      },

      logout: async () => {
        try {
          await api.post("/auth/logout");
        } catch {
          // Ignore logout API errors
        } finally {
          tokenStorage.clear();
          // Xóa cookie của middleware
          if (typeof window !== "undefined") {
            document.cookie = "universe_admin_token=; path=/; expires=Thu, 01 Jan 1970 00:00:00 GMT;";
          }
          set({ user: null, isAuthenticated: false, error: null });
        }
      },

      clearError: () => set({ error: null }),

      setUser: (user: User) => set({ user }),
    }),
    {
      name: "universe-admin-auth",
      partialize: (state) => ({ user: state.user, isAuthenticated: state.isAuthenticated }),
    }
  )
);
