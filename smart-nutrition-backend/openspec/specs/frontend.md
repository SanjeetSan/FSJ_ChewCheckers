# ChewChecker AI — Frontend Living Spec

> This document describes the current state of the ChewChecker AI frontend application.
> Updated incrementally as features are added or modified.

---

## Overview

| Property | Value |
|---|---|
| **Project Name** | ChewChecker AI — Smart Nutrition Frontend |
| **Location** | `C:\Users\sanje\.gemini\antigravity\scratch\smart-nutrition-frontend` |
| **Tech Stack** | HTML5, Vanilla CSS, Vanilla JavaScript, Vite 5.x |
| **Dev Server** | `http://localhost:3000` (Vite) |
| **Backend Gateway** | `http://localhost:8080` (Spring Cloud Gateway) |
| **Design** | Mobile-first dark theme dashboard (centered on desktop) |

---

## Portal Roles & Views

The app has 3 portal modes switchable via dropdown (development feature for testing):

### Parent Portal
| View ID | Screen Title | Status |
|---|---|---|
| `parent-overview` | Parent Nutrition Dashboard | ✅ Functional |
| `ai-scanner` | AI Lunchbox Vision Scanner | ✅ Functional |
| `leftover-tracker` | Leftover & Actual Consumption Tracker | ✅ Functional |
| `ai-assistant` | AI Nutrition & Meal Recommendation Assistant | ✅ Functional |
| `messaging` | Direct Parent-Teacher Messages | ✅ Functional |

### Teacher Portal
| View ID | Screen Title | Status |
|---|---|---|
| `teacher-roster` | Teacher Class Roster & Intake Summary | ✅ Functional |
| `teacher-reports` | Class Nutrition Aggregate & Analytics | ✅ Functional |

### Admin Portal
| View ID | Screen Title | Status |
|---|---|---|
| `admin-users` | User Accounts & School Code Management | ✅ Functional |
| `admin-health` | Admin & System Health Diagnostics | ✅ Functional |

---

## API Endpoints Connected

| Action | Method | Endpoint | Service |
|---|---|---|---|
| Upload lunchbox photo | `POST` | `/api/meals/upload` | meal-service (8082) |
| Log leftover percentage | `POST` | `/api/meals/log-leftovers` | meal-service (8082) |
| Delete meal record | `DELETE` | `/api/meals/{id}` | meal-service (8082) |
| Ask AI diet question | `POST` | `/api/assistant/ask` | meal-service (8082) |
| Send message | `POST` | `/api/messages` | messaging-service (8084) |
| Register user | `POST` | `/api/auth/register` | auth-service (8081) |
| Login user | `POST` | `/api/auth/login` | auth-service (8081) |

---

## Key UI Systems

### Toast Notification System
- Container: `#toastContainer`
- Function: `showToast(message, type)`
- Animation: `@keyframes toastIn` / `toastOut` (3.2s auto-dismiss)
- Types: success (green), error (red), info (blue)

### Modal System
- User CRUD Modal: `#userModalOverlay`
- Fields: Full Name, Email, Role (PARENT/TEACHER/ADMIN), Class Code
- Actions: Add New User, Edit User, Deactivate User

### Persistent State (localStorage)
- `chewchecker_meal_history` — Scan history survives refresh; deletions are permanent

---

## Files

| File | Size | Purpose |
|---|---|---|
| `index.html` | ~46KB | All HTML views (9 sections), modal overlay, toast container |
| `styles.css` | ~21KB | Full CSS: dark theme, cards, sidebar, toast animations, modal |
| `app.js` | ~40KB | All JS logic: view switching, API calls, Chart.js, CRUD, toasts |
| `vite.config.js` | 237B | Dev server (port 3000) + `/api` proxy to gateway |
| `package.json` | 724B | Vite + dependencies |

---

*Last Updated: 2026-08-10, 6:58 PM IST*
