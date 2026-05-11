# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

UniVerse is a Smart Campus Ecosystem — a university management platform connecting Admin, Lecturer, and Student actors in real-time. The project is currently in early UI prototyping stage with hardcoded data and no backend integration yet.

## Repository Layout

This is NOT a monorepo. Each app is an independent project with its own `node_modules` and `package.json`.

**Active apps (have dependencies installed):**
- `admin-web/` — Admin Portal (Next.js 16, React 19, TailwindCSS v4)
- ``lecturer-web/` — Lecturer Portal (Next.js 16, React 19, TailwindCSS v4)

**Planned apps (skeleton only, no dependencies installed):**
- `apps/server/` — NestJS backend
- `apps/web/` — Next.js web portal (stub dirs only)
- `apps/mobile/` — React Native app
- `apps/ai-service/` — Python Flask AI chatbot

## Common Commands

Run from the respective app directory (`admin-web/` or `lecturer-web/`):

```bash
npm run dev      # Start dev server (Next.js)
npm run build    # Production build
npm run start    # Start production server
npm run lint     # ESLint
```

No test framework is configured — there are no test commands or test files.

## Architecture

### Frontend (admin-web, lecturer-web)

- **App Router** with route groups: `src/app/(dashboard)/` for authenticated pages
- All components are `"use client"` — fully client-side rendered
- Path alias: `@/*` maps to `./src/*`
- Data is hardcoded inline — no API calls, no state management library, no HTTP client
- Theme: light/dark via `ThemeProvider.tsx` (localStorage persistence)

### Intended Backend (not yet built)

Per `AGENT.md`, the backend should follow:
- **NestJS** with Controller → Service → Repository pattern, DTOs for validation
- **TypeORM** for PostgreSQL (Users, Roles, Courses, Classes, Schedules, Attendances, Grades, Enrollments)
- **MongoDB** for documents (messages, notifications, activity_logs)
- **Redis** for sessions, QR token cache, schedule cache
- **Kafka** + **Socket.IO** for real-time notifications

### Core Business Logic

**Smart Attendance** requires two simultaneous verifications:
1. Dynamic QR Code — server generates HMAC-SHA256 signed token with `session_id` + `timestamp`, rotates every 5 seconds
2. GPS Geo-fencing — student sends GPS coordinates, server validates distance using **Haversine formula** (threshold: ≤ 50 meters)

**Notification Pipeline:** API → MongoDB → Kafka topic `class-notifications` → Socket.IO (online users) / Firebase FCM (offline users)

### Database Boundaries

| Store | Data | ORM |
|-------|------|-----|
| PostgreSQL | Structured/relational (Users, Courses, Classes, Schedules, Grades) | TypeORM |
| MongoDB | Documents (messages, notifications, activity_logs) | — |
| Redis | Sessions, QR tokens, schedule cache | — |

## Design System

- **TailwindCSS v4** with `@tailwindcss/postcss`
- Brand color: `#4d41df` (purple)
- Font: Inter (`next/font/google`), Icons: Material Symbols Outlined
- Custom typography: `text-h1` through `text-label` utilities
- 4px spacing grid: xs=4, sm=8, md=16, lg=24, xl=32

## AI Coding Rules (from AGENT.md)

- All code must be TypeScript with explicit types/interfaces
- NestJS: strict Controller → Service → Repository flow, mandatory DTOs
- GPS distance: implement real Haversine algorithm, no mocks
- Passwords: `bcrypt` hashing
- Auth: `JwtAuthGuard` + `RolesGuard` per actor role
- HTTP errors must be explicit with clear codes (e.g., distance exceeded, QR expired)

## Next.js Note

This project uses Next.js 16.2.6 which may have breaking changes from earlier versions. Check `node_modules/next/dist/docs/` before writing code if you're unsure about API compatibility.
