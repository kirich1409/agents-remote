FROM gradle:8.6-jdk21 as builder
WORKDIR /app
COPY . .
RUN gradle :backend:build -x test --no-daemon

FROM eclipse-temurin:25-jre-alpine
RUN apk add --no-cache curl
WORKDIR /app
COPY --from=builder /app/backend/build/libs/backend.jar app.jar
EXPOSE 3000
CMD ["java", "-jar", "app.jar"]
