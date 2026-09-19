# 🍱 Smart Nutrition Monitoring System — Startup & Migration Guide

Welcome! Follow this guide to set up and run the complete **Smart Nutrition Monitoring System** (Spring Boot Microservices + Vite/JS Frontend + MySQL Database) on your machine.

---

## 📋 1. Environment & Prerequisites

Ensure the following tools are installed on your system:

| Dependency | Minimum Version | Verification Command |
| :--- | :--- | :--- |
| **Java JDK** | OpenJDK 17 or 21 | `java -version` |
| **Node.js** | Node.js v18 LTS or v20 LTS | `node -v` |
| **npm** | v9+ or v10+ | `npm -v` |
| **MySQL Server** | MySQL 8.0+ (Port 3306) | `mysql --version` |

---

## 🗄️ 2. Database Import (MySQL)

1. Open MySQL Command Line or MySQL Workbench and create the database:
   ```sql
   CREATE DATABASE IF NOT EXISTS smart_nutrition_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
   ```
2. Import the provided data dump via PowerShell / Terminal:
   ```powershell
   mysql -u root -p smart_nutrition_db < smart_nutrition_db_full_dump.sql
   ```
   *(Enter your local MySQL password when prompted)*

---

## 🔑 3. Configuration & Secrets Setup

Open `smart-nutrition-backend/secrets.properties` and verify your credentials:

```properties
GEMINI_API_KEY=YOUR_GEMINI_API_KEY_HERE
DB_USERNAME=root
DB_PASSWORD=YOUR_LOCAL_MYSQL_PASSWORD
```
> 💡 **Note**: Update `DB_PASSWORD` to match your local MySQL root password.

---

## 🚀 4. Startup Order (Spring Boot Microservices)

Open separate terminal windows and launch the microservices in this exact sequence:

### Step 1: Eureka Discovery Server (Port 8761)
```powershell
cd smart-nutrition-backend/smart-nutrition-eureka-server
.\mvnw spring-boot:run
```
*Wait until output displays: `Started EurekaServerApplication`*

### Step 2: API Gateway (Port 8080)
```powershell
cd smart-nutrition-backend/smart-nutrition-gateway
.\mvnw spring-boot:run
```

### Step 3: Auth & User Service (Port 8081)
```powershell
cd smart-nutrition-backend/smart-nutrition-auth-service
.\mvnw spring-boot:run
```

### Step 4: School & Classroom Service (Port 8082)
```powershell
cd smart-nutrition-backend/smart-nutrition-school-service
.\mvnw spring-boot:run
```

### Step 5: Meal & AI Vision Service (Port 8083)
```powershell
cd smart-nutrition-backend/smart-nutrition-meal-service
.\mvnw spring-boot:run
```

### Step 6: Messaging & Parent Alert Service (Port 8084)
```powershell
cd smart-nutrition-backend/smart-nutrition-messaging-service
.\mvnw spring-boot:run
```

---

## 💻 5. Launch Frontend Web Application

Open a new terminal window:
```powershell
cd smart-nutrition-frontend
npm install
npm run dev
```
Open your browser at: **`http://localhost:5173`** (or the URL printed in the terminal).

---

## 🔑 6. Pre-Configured Test Login Accounts

The imported database includes all fully configured role accounts:

| Role | Email | Password | Features Accessible |
| :--- | :--- | :--- | :--- |
| **Parent** | `dharun@gmail.com` | `sanjeet` | Dashboard, Children Profiles, Lunchbox Presets, Lunch Scanner, Reports, AI Assistant |
| **Teacher** | `jothi@greenwood.edu` | `sanjeet` | Class Overview, Student Roster, Nutrition Compliance, Parent Messages |
| **Admin** | `admin@chewcheckers.com` | `sanjeet` | System User Management, School Settings, Audit Logs |

---

## ✅ 7. Quick Sanity Verification

1. **Eureka Dashboard**: Visit `http://localhost:8761` and verify all 5 microservices show status **UP**.
2. **Parent Portal Login**: Log in as `dharun@gmail.com`.
3. **AI Assistant Check**: Navigate to **AI Assistant** and send a prompt. Verify real-time response generation.
4. **Scanned Images Check**: Check **Recent Lunch Scans** to verify meal images render correctly from `uploads/`.
