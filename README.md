# Remote Cloud Code (RCC)

Kotlin Multiplatform chat application with a Ktor backend and Compose Multiplatform frontend. The project follows clean architecture with shared domain logic across Android, Desktop, and server targets.

## Project Structure

```
remote-cloud-code/
├── shared/          # Domain entities, use cases, repository interfaces (KMP)
├── backend/         # Ktor server with REST API and WebSocket support
├── app/             # Compose Multiplatform frontend (Android + Desktop)
├── Dockerfile       # Multi-stage Docker build for backend
└── docker-compose.yml
```

## Tech Stack

- **Kotlin Multiplatform** - shared domain logic
- **Ktor 3.4** - backend HTTP server with CIO engine
- **Compose Multiplatform** - UI for Android and Desktop
- **Decompose 3.4** - component-based navigation
- **MVIKotlin 4.3** - MVI state management
- **Koin** - dependency injection
- **SQLDelight** - local database
- **kotlinx.serialization** - JSON serialization

## Prerequisites

- JDK 21
- Android SDK (for Android builds)
- Docker (for backend deployment)

## Development Setup

```bash
# Clone
git clone https://github.com/kirich1409/agents-remote.git
cd agents-remote

# Run all quality checks
./gradlew codeQualityCheck

# Run tests
./gradlew test
```

### Backend

```bash
# Run backend server (port 3000)
./gradlew :backend:run

# Build fat JAR
./gradlew :backend:buildFatJar
```

### Android

```bash
# Build debug APK
./gradlew :app:assembleDebug

# Build release APK
./gradlew :app:assembleRelease
```

### Desktop

```bash
# Run desktop app
./gradlew :app:run
```

## Docker Deployment

```bash
# Build and start
docker compose up -d

# Verify
curl http://localhost:3000/health

# Stop
docker compose down
```

Environment variables for Docker:
- `AUTH_TOKEN` - authentication token
- `GATEWAY_PORT` - server port (default: 3000)
- `LOG_LEVEL` - logging level (default: INFO)

## API

### REST Endpoints

| Method | Path | Description |
|--------|------|-------------|
| GET | `/health` | Health check |
| GET | `/api/chats` | List all chats |
| POST | `/api/chats` | Create a new chat |
| DELETE | `/api/chats/{id}` | Delete a chat |
| GET | `/api/chats/{id}/messages` | Get chat messages |

### WebSocket

Connect to `/ws/chats/{chatId}` for real-time messaging.

## Code Quality

The project enforces strict code quality through a 5-layer protection system:

1. **Pre-commit hooks** - spotless, detekt, build, tests
2. **GitHub Actions CI** - quality checks, tests, security analysis, CodeQL
3. **Branch protection** - required status checks and reviews
4. **Code owners** - automatic review assignment
5. **Explicit API mode** - all public declarations require visibility modifiers

```bash
# Format code
./gradlew spotlessApply

# Run detekt
./gradlew detekt

# Full quality suite
./gradlew codeQualityCheck
```

## License

All rights reserved.
