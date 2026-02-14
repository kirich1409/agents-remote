FROM gradle:8.6-jdk21 as builder
WORKDIR /app
COPY . .
RUN gradle :backend:build -x test --no-daemon

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=builder /app/backend/build/libs/*.jar app.jar
EXPOSE 3000
CMD ["java", "-jar", "app.jar"]
