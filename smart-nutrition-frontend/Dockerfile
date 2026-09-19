# Multi-stage build for React frontend
# Stage 1: Build
FROM node:20-alpine AS builder
WORKDIR /build
COPY package*.json ./
RUN npm ci
COPY . .
RUN npm run build

# Stage 2: Serve with nginx
FROM nginx:alpine
WORKDIR /usr/share/nginx/html
COPY --from=builder /build/dist .
COPY nginx.conf /etc/nginx/conf.d/default.conf
EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]
