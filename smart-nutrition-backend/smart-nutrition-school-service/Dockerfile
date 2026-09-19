FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
RUN addgroup -S spring && adduser -S spring -G spring
COPY ./smart-nutrition-school-service/target/*.jar app.jar
RUN chown -R spring:spring /app
USER spring
EXPOSE 8083
ENTRYPOINT ["java", "-jar", "app.jar"]
