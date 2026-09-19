FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
RUN addgroup -S spring && adduser -S spring -G spring
COPY ./smart-nutrition-gateway/target/*.jar app.jar
RUN chown -R spring:spring /app && chmod +x app.jar
USER spring
EXPOSE 8088
ENTRYPOINT ["java", "-jar", "app.jar"]
