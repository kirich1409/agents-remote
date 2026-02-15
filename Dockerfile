FROM gradle:9.3-jdk21 AS builder
WORKDIR /app
COPY . .
RUN gradle :backend:buildFatJar --no-daemon

FROM eclipse-temurin:25-jre-alpine
RUN apk add --no-cache curl
WORKDIR /app
COPY --from=builder /app/backend/build/libs/backend-all.jar app.jar
EXPOSE 3000
CMD ["java", "-jar", "app.jar"]
