# 🍱 ChewCheckers — Full Stack AI School Lunchbox & Nutrition Platform

> **ChewCheckers (FSJ)** is an enterprise-grade school lunchbox and child nutrition tracking system powered by Spring Boot Microservices, Eureka Service Discovery, Spring Cloud API Gateway, MySQL, and a responsive modern frontend with AI-assisted meal analysis.

---

## 🌟 Key Highlights

- **AI-Calibrated Lunchbox Analysis**: Pre-meal and post-meal portion tracking, calorie/macronutrient breakdown, and automated meal clearance scoring.
- **Role-Based Access Control**:
  - 👨‍👩‍👧 **Parent Portal**: Track children's meals, view clearance trends, register custom lunchbox presets, link with school classes via secure join codes.
  - 👩‍🏫 **Teacher Classroom Suite**: Class meal rosters, quick student intake logging, attendance/nutrition summaries, and automated alerts.
  - 🛡️ **Administrator Dashboard**: System health diagnostics (Eureka, Gateway, Auth, School, Meal services), user administration, and academic calendar holiday management.
- **Enterprise Microservices Architecture**: Decoupled domain services with Eureka service registry, Cloud API Gateway, JWT security, and centralized exception handling.

---

## 🏗️ System Architecture

```
                                  +---------------------------+
                                  |    Vercel / Browser UI    |
                                  | (smart-nutrition-frontend)|
                                  +-------------+-------------+
                                                |
                                                v
                                  +---------------------------+
                                  |    API Gateway (:8088)    |
                                  +-------------+-------------+
                                                |
           +--------------------+---------------+--------------------+
           |                    |               |                    |
           v                    v               v                    v
+--------------------+ +----------------+ +----------------+ +----------------+
| Auth Service       | | School Service | | Meal Service   | | Messaging Svc  |
| (:8081)            | | (:8083)        | | (:8082)        | | (:8084)        |
+--------------------+ +----------------+ +----------------+ +----------------+
           |                    |               |                    |
           +--------------------+---------------+--------------------+
                                |
                                v
               +----------------------------------+
               |      Eureka Server (:8761)       |
               +----------------------------------+
               |        MySQL Database            |
               |     (smart_nutrition_db)         |
               +----------------------------------+
```

---

## 📁 Repository Structure

```
FSJ_ChewCheckers/
├── smart-nutrition-frontend/    # Vite + Vanilla JS Production SPA
│   ├── index.html               # Main single-page application UI
│   ├── app.js                   # Application state, views, and API client
│   ├── styles.css               # Design system & responsive styles
│   ├── vite.config.js           # Vite development and proxy config
│   ├── vercel.json              # Vercel SPA routing and build configuration
│   └── package.json             # Frontend dependencies and scripts
│
├── smart-nutrition-backend/     # Spring Boot Microservices
│   ├── smart-nutrition-eureka-server/     # Eureka Service Discovery (:8761)
│   ├── smart-nutrition-gateway/           # Spring Cloud Gateway (:8088)
│   ├── smart-nutrition-auth-service/      # Authentication & User Management (:8081)
│   ├── smart-nutrition-school-service/    # School, Classes & Presets (:8083)
│   ├── smart-nutrition-meal-service/      # Meals, Nutrition & AI (:8082)
│   ├── smart-nutrition-messaging-service/ # Notifications & Messaging (:8084)
│   └── smart-nutrition-common/            # Shared DTOs, Entities & Utilities
│
├── docs/                        # Complete SRS, Gap Analysis & Setup Guides
├── smart_nutrition_db_full_dump.sql # Full database schema & seed data
├── vercel.json                  # Root Vercel build configuration
└── package.json                 # Root monorepo scripts
```

---

## 🚀 Deploying to Vercel

The frontend is fully configured and ready for 1-click deployment on **Vercel**:

### Option 1: Direct Root Import (Recommended)
1. In Vercel, click **Add New Project** and select this repository (`FSJ_ChewCheckers`).
2. Leave the **Root Directory** as `./` (the root `vercel.json` and `package.json` automatically orchestrate the build).
3. Under **Environment Variables**, configure:
   - `VITE_GATEWAY_URL`: URL of your deployed ChewCheckers API Gateway (e.g., `https://api.yourdomain.com`).
4. Click **Deploy**.

### Option 2: Set Root Directory to Frontend
1. In Vercel Project Settings, set **Root Directory** to `smart-nutrition-frontend`.
2. Framework Preset will auto-detect as **Vite**.
3. Under **Environment Variables**, configure:
   - `VITE_GATEWAY_URL`: URL of your deployed API Gateway.
4. Click **Deploy**.

---

## 💻 Local Development Setup

### 1. Database Setup
1. Ensure MySQL is running on `localhost:3306`.
2. Import the provided schema:
   ```bash
   mysql -u root -p < smart_nutrition_db_full_dump.sql
   ```

### 2. Microservices Backend
Start services in the following sequence:
1. **Eureka Server**: `smart-nutrition-eureka-server` (`mvn spring-boot:run`)
2. **API Gateway**: `smart-nutrition-gateway` (`mvn spring-boot:run`)
3. **Core Services**:
   - `smart-nutrition-auth-service`
   - `smart-nutrition-school-service`
   - `smart-nutrition-meal-service`
   - `smart-nutrition-messaging-service`

### 3. Frontend
```bash
cd smart-nutrition-frontend
npm install
npm run dev
```
Open [http://localhost:3000](http://localhost:3000) in your browser.

---

## 📄 License
This project is developed as part of the ChewCheckers Full Stack Java initiative.
