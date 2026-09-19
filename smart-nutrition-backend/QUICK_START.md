# Smart Nutrition - Quick Start Guide

## Prerequisites
- Docker Desktop installed and running
- Project at: `C:\Users\sanje\.gemini\antigravity\scratch\smart-nutrition-backend`

## EASY 3-STEP STARTUP

### Step 1: Navigate to Project
```bash
cd C:\Users\sanje\.gemini\antigravity\scratch\smart-nutrition-backend
```

### Step 2: Rebuild All Images (One-Time Only)
```bash
./mvnw clean install -DskipTests
```
This builds all 7 Docker images (takes ~5-10 min first time).

### Step 3: Start the Stack
```bash
docker compose up -d
```

### Step 4: Wait for Services to Start
```bash
docker ps
```
Wait until all containers show "Up" status (takes ~30-60 sec after start).

---

## Access Your Application

| Service | URL | Purpose |
|---------|-----|---------|
| **Frontend** | http://localhost | React UI |
| **API Gateway** | http://localhost:8088 | REST API entry point |
| **Eureka Registry** | http://localhost:8761 | Service registry |
| **Auth Service** | http://localhost:8081 | Authentication microservice |
| **Meal Service** | http://localhost:8082 | Meal management microservice |
| **School Service** | http://localhost:8083 | School management microservice |
| **Messaging Service** | http://localhost:8084 | Messaging microservice |

---

## Stop the Stack
```bash
docker compose down
```

## View Logs
```bash
docker logs smart-nutrition-auth
docker logs smart-nutrition-eureka
docker logs smart-nutrition-gateway
```

## Full Cleanup (Delete All Data)
```bash
docker compose down -v
```

---

## Troubleshooting

**Services not starting?**
- Check logs: `docker logs <service-name>`
- Ensure ports 80, 8081-8084, 8088, 8761 are free
- Restart Docker Desktop

**Database connection errors?**
- Wait 30 seconds for databases to initialize
- Check if PostgreSQL containers are healthy: `docker ps` (look for "healthy" status)

**Can't access frontend?**
- Ensure frontend container is running: `docker ps | grep frontend`
- Try `http://localhost:80` instead of just `http://localhost`

