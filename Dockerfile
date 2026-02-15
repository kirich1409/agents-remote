# Stage 1: Build fat JAR
FROM gradle:9.3-jdk21 AS builder
WORKDIR /app
COPY . .
RUN gradle :backend:buildFatJar --no-daemon

# Stage 2: Runtime with Node.js and Claude CLI
FROM eclipse-temurin:21-jre
WORKDIR /app

# Install curl and Node.js 22.x
RUN apt-get update && \
    apt-get install -y --no-install-recommends curl ca-certificates gnupg && \
    mkdir -p /etc/apt/keyrings && \
    curl -fsSL https://deb.nodesource.com/gpgkey/nodesource-repo.gpg.key | gpg --dearmor -o /etc/apt/keyrings/nodesource.gpg && \
    echo "deb [signed-by=/etc/apt/keyrings/nodesource.gpg] https://deb.nodesource.com/node_22.x nodistro main" > /etc/apt/sources.list.d/nodesource.list && \
    apt-get update && \
    apt-get install -y --no-install-recommends nodejs && \
    apt-get clean && \
    rm -rf /var/lib/apt/lists/*

# Install Claude CLI
RUN npm install -g @anthropic-ai/claude-code

# Copy fat JAR from builder
COPY --from=builder /app/backend/build/libs/backend-all.jar app.jar

# Create data directory for SQLite volume
RUN mkdir -p /app/data

EXPOSE 8080

HEALTHCHECK --interval=30s --timeout=10s --retries=3 \
    CMD curl -f http://localhost:${GATEWAY_PORT:-8080}/health || exit 1

CMD ["java", "-jar", "app.jar"]
