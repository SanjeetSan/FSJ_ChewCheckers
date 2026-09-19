# 🍱 Smart Nutrition Platform – Project Overview

Welcome to the **Smart Nutrition Platform**! This is an AI-powered school lunchbox monitoring and child nutrition tracking system designed for **Parents**, **Teachers**, and **School Administrators**.

---

## 🌟 What Does This Project Do?

Packing a healthy lunch for kids is easy, but knowing how much they actually ate and whether they get enough protein, vitamins, and calories is hard. 

This platform solves that problem using **Artificial Intelligence (Google Gemini 1.5 Flash Vision)**:
1. **Snap a Photo:** A parent or teacher snaps a picture of the child's lunchbox.
2. **AI Magic:** The AI automatically recognizes the food items (e.g. *Wheat Roti*, *Vegetable Sabzi*, *Burger*, *Salad*) and calculates exact nutritional facts (Calories, Protein, Carbs, Fat, Fiber).
3. **Track Intake:** Parents log what was left over after lunch, and the system automatically calculates the exact nutrients the child consumed!
4. **School & Teacher Reports:** Teachers and school managers get weekly and monthly health reports for their classes.

---

## 👥 Key Features by User Role

### 1. 👨‍👩‍👧 Parents
- **1-Time Class Onboarding:** Enter your child's class code (e.g. `CLS-3A`) once during setup to automatically link your child to their class and teacher.
- **Lunch Photo Upload:** Upload a photo of the lunchbox and get instant AI nutrition details.
- **Granular Leftover Tracking:** Record leftover percentages per food item so you know what your child ate.
- **Direct Teacher Chat:** Private 1-on-1 messaging with teachers to discuss allergies or diet updates.
- **AI Nutrition Assistant:** Ask questions like *"What high-protein snack can I pack tomorrow?"* and get smart recommendations.

### 2. 👩‍🏫 Teachers
- **Class Roster Overview:** View all students linked to your class codes (`CLS-3A`, `CLS-4B`).
- **Weekly & Monthly Reports:** View aggregate nutrition summaries, class average intake, and waste rates.
- **Nutritional Warning Alerts:** Get automatic notifications if class leftover rates are high or if a student is missing meals.
- **Parent Communication:** Send and receive direct messages with parents.

### 3. 🛡️ School Administrators & Developers (Management / Admin)
- **User & Role Management:** View all accounts in the school and promote teachers to **School Administrators**.
- **Account Control:** Manage and deactivate accounts when necessary.
- **System Health Diagnostics:** Monitor database connectivity, registered user counts, and total logged meals.
- **Developer Privileges:** Default developer account (`sanjeet...`) automatically receives full administrative access for debugging and feature updates.

---

## 🔒 Security & Privacy Features

- **JWT Authentication:** All requests use secure, stateless Bearer Tokens.
- **Protected Secrets:** Your Google Gemini API Key is safely stored in a local `secrets.properties` file that is excluded from GitHub via `.gitignore`.
- **Case-Insensitive Class Codes:** Parents can enter class codes in any case (e.g. `cls-3a` or `CLS-3A`) for error-free setup.

---

## 🚀 How to Run the Project locally

### Prerequisites:
- **Java 21** installed
- **MySQL Database** running on `localhost:3006` (Database name: `smart_nutrition_db`)

### Steps:
1. Open PowerShell and navigate to the project directory:
   ```powershell
   cd C:\Users\sanje\.gemini\antigravity\scratch\smart-nutrition-backend
   ```
2. Start the Spring Boot server:
   ```powershell
   .\mvnw spring-boot:run
   ```
3. Open **Swagger UI** in your web browser to test all APIs:
   👉 **`http://localhost:8081/swagger-ui.html`**

---

## 🛠️ Technology Stack Used

- **Backend:** Java 21, Spring Boot 3.x (Spring Security, Spring Data JPA)
- **Database:** MySQL
- **AI Engine:** Google Gemini 1.5 Flash API (Multimodal Vision & Text)
- **Documentation & Testing:** Swagger UI (OpenAPI 3.0), PowerShell Test Suite
- **Version Control:** Git & GitHub (`https://github.com/SanjeetSan/smart-nutrition.git`)
