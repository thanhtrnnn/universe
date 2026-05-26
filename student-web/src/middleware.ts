import { NextResponse } from "next/server";
import type { NextRequest } from "next/server";

// Routes không cần auth
const PUBLIC_PATHS = ["/login", "/forgot-password"];

export function middleware(request: NextRequest) {
  const { pathname } = request.nextUrl;

  // Bỏ qua static files và api routes
  if (
    pathname.startsWith("/_next") ||
    pathname.startsWith("/api") ||
    pathname.startsWith("/favicon")
  ) {
    return NextResponse.next();
  }

  const isPublic = PUBLIC_PATHS.some((p) => pathname.startsWith(p));

  // Lấy token từ cookie (server-side) hoặc check localStorage (client sẽ redirect)
  // Vì localStorage không accessible ở middleware, dùng cookie
  const token = request.cookies.get("universe_student_token")?.value;

  // Nếu chưa login và truy cập route bảo vệ → redirect login
  if (!isPublic && !token) {
    const url = request.nextUrl.clone();
    url.pathname = "/login";
    url.searchParams.set("from", pathname);
    return NextResponse.redirect(url);
  }

  // Nếu đã login và vào trang login → redirect dashboard
  if (isPublic && token) {
    const url = request.nextUrl.clone();
    url.pathname = "/";
    return NextResponse.redirect(url);
  }

  return NextResponse.next();
}

export const config = {
  matcher: ["/((?!_next/static|_next/image|favicon.ico).*)"],
};
