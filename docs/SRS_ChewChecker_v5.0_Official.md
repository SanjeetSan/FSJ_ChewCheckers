# SYSTEM REQUIREMENTS SPECIFICATION (SRS)
## Full Stack Java (AI-Integrated) Training Programme

| Parameter | Project Details |
| :--- | :--- |
| **Project Title** | Smart Nutrition Monitoring Lunchbox for School Children |
| **SIH Problem Statement ID** | SIH25-098 |
| **Ministry / Organisation** | Health / Nutrition Domain |
| **Domain Category** | Health / Nutrition |
| **Team Name** | Vanguard Void |
| **Team Members** | 1. Raja SreeDharun<br>2. Sanjeet S |
| **Institution** | Karpagam College Of Engineering |
| **Project Type** | [ ✔ ] Software |
| **Version** | v5.0 (Strict Code-Aligned Update) |
| **Date** | 13/08/2026 |
| **Faculty Mentors** | Dr. Arul Antran Vijay S / Dr. Jothi Prakash V / Mr. Jegathesh P / Mr. Navaneetha Krishnan M / Dr. Castro S. |

---

## 📄 DOCUMENT REVISION HISTORY & VERSION EVOLUTION

| Version | Date | Author | Core Scope & Architectural Milestones |
| :---: | :---: | :---: | :--- |
| **v1.0** | 10/06/2026 | Team Vanguard Void | Initial Problem Statement SIH25-098 framing, IEEE Std 830 skeleton, user roles (Parent, Teacher, Admin), basic lunchbox concept. |
| **v2.0** | 18/06/2026 | Team Vanguard Void | Microservices Architecture — Spring Cloud Gateway (8088), Eureka Server (8761), Auth Service (8081), BCrypt hashing, JWT Security & MySQL `users` table. |
| **v3.0** | 02/07/2026 | Team Vanguard Void | Parent Onboarding Flow (`ParentService`), Child Profiles (`students`), Classroom Setup & Join Codes (`classes`, `PUT /api/parent/student/{id}/class`, `POST /api/teacher/classes/{id}/generate-code`) & Chat (`messages`). |
| **v4.0** | 20/07/2026 | Team Vanguard Void | Lunchbox Physical Modeling ($L \times W \times H$ or $\pi r^2 h$, compartments, shapes), Configurable Lunch Target Calculator (0.35 ratio), Photo Scans & Leftover Tracking (`POST /api/meals/post-meal`). |
| **v5.0** | 13/08/2026 | Team Vanguard Void | AI Integration & Insights — Google Gemini 1.5-flash REST Integration (requires `GEMINI_API_KEY`), `nutrition_scores` evaluation (`NutritionScoringService`), `NutritionInsightService`, `AssistantController`, Capacity Validation & UI Polish. |

---

# 1. INTRODUCTION

### 1.1 Purpose
This System Requirements Specification (SRS) document details the exact implemented functional and non-functional specifications for **ChewCheckers (Smart Nutrition Monitoring Lunchbox for School Children)** under SIH Problem Statement SIH25-098. It strictly reflects the physical backend Java microservices codebase, true server port allocations, exact JPA entities, exact Service class names, and true REST API contracts.

### 1.2 Scope
- **System Name**: ChewCheckers
- **System Description**: ChewCheckers is a smartphone-assisted lunchbox monitoring system. Parents and teachers upload photos of school lunchboxes before and after mealtime. A cloud-based AI engine powered by Google Gemini 1.5-flash performs image-based food recognition and nutritional intake estimation (integrated via REST API; requires `GEMINI_API_KEY` in `secrets.properties`). The backend calculates specific lunch calorie and protein targets using a configurable allocation ratio (default 0.35 via `nutrition.lunch.allocation-ratio=0.35`), tracks leftover consumption %, evaluates `nutrition_scores`, validates lunchbox capacity against targets, and provides a conversational AI Pediatric Nutrition Assistant (`AssistantController`).

---

# 5. FUNCTIONAL REQUIREMENTS

### 5.3 LUNCHBOX PHYSICAL MODELING & TARGET ENGINE

#### FR-TGT-001: Dedicated Lunchbox Physical & Compartment Modeling
- **Description**: Models physical lunchboxes directly inside the `Student` entity and `lunchbox_presets` table using 6 physical parameters:
  - `boxLength` (Decimal, cm)
  - `boxWidth` (Decimal, cm)
  - `boxDepth` (Decimal, cm)
  - `boxVolume` (Decimal, cm³)
  - `boxShape` (String: `Rectangular` vs `Circular`)
  - `boxCompartments` (Integer: 1, 2, 3, or 4 compartments)
- **Volume Formulae**:
  - Rectangular Box: $\text{Volume} = \text{boxLength} \times \text{boxWidth} \times \text{boxDepth}\text{ cm}^3$
  - Circular Box: $\text{Volume} = \pi \times (\text{boxWidth}/2)^2 \times \text{boxDepth}\text{ cm}^3$
- **Actor**: Parent
- **Files**: `Student.java`, `ParentService.java`, `LunchboxPresetService.java`

#### FR-TGT-002: Configurable Lunch Nutrition Target Engine
- **Description**: Computes specific lunch calorie and protein requirements using a configurable allocation ratio (default 0.35 via `nutrition.lunch.allocation-ratio=0.35`):
  $$\text{Lunch Calorie Target} = \text{Daily Calorie Target} \times \text{nutrition.lunch.allocation-ratio}$$
  $$\text{Lunch Protein Target} = \text{Daily Protein Target} \times \text{nutrition.lunch.allocation-ratio}$$
- **Actor**: System / Service Layer
- **Files**: `ParentService.java`, `Student.java`

---

# 8. DATABASE SCHEMA (PHYSICAL TABLES IN CODE)

The relational database `smart_nutrition_db` runs on MySQL 8.0 port `3306` comprising 10 physical relational tables managed by JPA entities:
1. `users` (User.java)
2. `schools` (School.java)
3. `classes` (Class_.java)
4. `students` (Student.java: includes `box_length`, `box_width`, `box_depth`, `box_volume`, `box_shape`, `box_compartments`)
5. `parent_students` (ParentStudent.java)
6. `lunchbox_presets` (LunchboxPreset.java)
7. `meals` (Meal.java)
8. `meal_food_items` (MealFoodItem.java)
9. `nutrition_scores` (NutritionScore.java)
10. `messages` (Message.java)

---

## 🎯 END OF STRICT CODE-ALIGNED SRS DOCUMENTATION — Version 5.0
