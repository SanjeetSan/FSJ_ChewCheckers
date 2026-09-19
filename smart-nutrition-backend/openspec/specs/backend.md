# ChewChecker AI — Backend Living Spec

> This document describes the current state of the ChewChecker AI backend microservices.
> Updated incrementally as services are added or modified.

---

## Overview

| Property | Value |
|---|---|
| **Project Name** | Smart Nutrition Platform (ChewChecker AI) |
| **Location** | `C:\Users\sanje\.gemini\antigravity\scratch\smart-nutrition-backend` |
| **GitHub** | `https://github.com/SanjeetSan/smart-nutrition.git` |
| **Tech Stack** | Java 21, Spring Boot 3.2.2, Spring Cloud 2023.0.0 |
| **Database** | MySQL (`localhost:3306/smart_nutrition_db`) |
| **AI Engine** | Google Gemini 1.5 Flash API (Multimodal Vision & Text) |
| **Auth** | JWT (stateless Bearer Tokens) |
| **Build** | Maven multi-module (`pom` packaging) |
| **Docs** | Swagger UI (OpenAPI 3.0) aggregated at Gateway |

---

## Architecture

```
                    ┌──────────────────┐
                    │  EUREKA-SERVER   │
                    │    Port 8761     │
                    └────────┬─────────┘
                             │ (Service Discovery)
          ┌──────────────────┼──────────────────────┐
          │                  │                      │
┌─────────▼──────┐ ┌────────▼────────┐ ┌───────────▼────────┐
│ API-GATEWAY    │ │ AUTH-ADMIN      │ │ AI-MEAL-VISION     │
│ Port 8080      │ │ Port 8081       │ │ Port 8082          │
│ (Routes + JWT  │ │ (Register/Login │ │ (Photo Upload,     │
│  + Swagger     │ │  + User CRUD)   │ │  Gemini Vision,    │
│  Aggregation)  │ │                 │ │  Leftover Tracking)│
└────────────────┘ └─────────────────┘ └────────────────────┘
          │
          ├─────────────────┐──────────────────┐
          │                 │                  │
┌─────────▼──────┐ ┌───────▼─────────┐ ┌──────▼──────────────┐
│ SCHOOL-REPORT  │ │ MESSAGING-      │ │ smart-nutrition-    │
│ Port 8083      │ │ SOCIAL          │ │ common              │
│ (Class Roster, │ │ Port 8084       │ │ (Shared DTOs,       │
│  Reports, PDF) │ │ (Parent-Teacher │ │  Utilities)         │
└────────────────┘ │  Chat)          │ └─────────────────────┘
                   └─────────────────┘
```

---

## Services Detail

### 1. EUREKA-SERVER (`smart-nutrition-eureka-server`)
| Property | Value |
|---|---|
| Port | `8761` |
| Spring App Name | `EUREKA-SERVER` |
| Self-Register | `false` |
| Fetch Registry | `false` |
| Self-Preservation | `false` |
| Dashboard | `http://localhost:8761` |

---

### 2. API-GATEWAY-SERVICE (`smart-nutrition-gateway`)
| Property | Value |
|---|---|
| Port | `8080` |
| Spring App Name | `API-GATEWAY-SERVICE` |
| Database | MySQL (`smart_nutrition_db`) |
| JWT Validation | ✅ Shared secret with all services |
| Swagger Aggregation | ✅ All 4 service docs at `/swagger-ui/index.html` |
| Actuator | `management.endpoints.web.exposure.include=*` |

**Swagger Aggregation Routes:**
| Name | Source URL |
|---|---|
| Auth & User Admin Service | `http://localhost:8081/v3/api-docs` |
| AI Meal & Vision Service | `http://localhost:8082/v3/api-docs` |
| School & Report Service | `http://localhost:8083/v3/api-docs` |
| Messaging & Social Service | `http://localhost:8084/v3/api-docs` |

---

### 3. AUTH-ADMIN-SERVICE (`smart-nutrition-auth-service`)
| Property | Value |
|---|---|
| Port | `8081` |
| Spring App Name | `AUTH-ADMIN-SERVICE` |
| Database | MySQL (`smart_nutrition_db`) |
| DDL Auto | `update` |
| SQL Init | `always` |
| JWT Expiry (Access) | 15 min (`900000ms`) |
| JWT Expiry (Refresh) | 7 days (`604800000ms`) |
| Gemini API Key | From `secrets.properties` |

---

### 4. AI-MEAL-VISION-SERVICE (`smart-nutrition-meal-service`)
| Property | Value |
|---|---|
| Port | `8082` |
| Spring App Name | `AI-MEAL-VISION-SERVICE` |
| Database | MySQL (`smart_nutrition_db`) |
| Image Upload Dir | `uploads/` |
| AI Engine | Google Gemini 1.5 Flash (Vision + Text) |

---

### 5. SCHOOL-REPORT-SERVICE (`smart-nutrition-school-service`)
| Property | Value |
|---|---|
| Port | `8083` |
| Spring App Name | `SCHOOL-REPORT-SERVICE` |
| Database | MySQL (`smart_nutrition_db`) |

---

### 6. MESSAGING-SOCIAL-SERVICE (`smart-nutrition-messaging-service`)
| Property | Value |
|---|---|
| Port | `8084` |
| Spring App Name | `MESSAGING-SOCIAL-SERVICE` |
| Database | MySQL (`smart_nutrition_db`) |

---

### 7. smart-nutrition-common (Shared Library)
- Shared DTOs, utility classes, and common configurations
- No runnable service (no port)
- Dependency for all other modules

---

## Shared Configuration (All Services)

| Property | Value |
|---|---|
| MySQL URL | `jdbc:mysql://localhost:3306/smart_nutrition_db?createDatabaseIfNotExist=true` |
| MySQL Username | `${DB_USERNAME:root}` |
| MySQL Password | `${DB_PASSWORD:sanjeet}` |
| Hibernate Dialect | `org.hibernate.dialect.MySQLDialect` |
| DDL Auto | `update` |
| JWT Secret | `404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970` |
| Eureka Zone | `http://localhost:8761/eureka/` |
| Prefer IP | `true` |
| Swagger Status Page | `/swagger-ui/index.html` |
| Actuator Endpoints | All exposed (`*`) |
| Secrets Import | `optional:file:../secrets.properties` |

---

## Port Map

| Service | Port | Eureka Name |
|---|---|---|
| Eureka Server | `8761` | — (standalone) |
| API Gateway | `8080` | `API-GATEWAY-SERVICE` |
| Auth Service | `8081` | `AUTH-ADMIN-SERVICE` |
| Meal Service | `8082` | `AI-MEAL-VISION-SERVICE` |
| School Service | `8083` | `SCHOOL-REPORT-SERVICE` |
| Messaging Service | `8084` | `MESSAGING-SOCIAL-SERVICE` |

---

## User Roles

| Role | Capabilities |
|---|---|
| **PARENT** | Upload lunchbox photos, track leftovers, chat with teacher, AI assistant |
| **TEACHER** | View class roster, weekly/monthly nutrition reports, message parents |
| **ADMIN** | User management, account control, system health, developer debug |

---

## Security

- All API calls require JWT Bearer Token (except `/api/auth/register` and `/api/auth/login`)
- Gemini API Key stored in `secrets.properties` (git-ignored)
- Class codes are case-insensitive (`cls-3a` == `CLS-3A`)

---

*Last Updated: 2026-08-10, 7:04 PM IST*
