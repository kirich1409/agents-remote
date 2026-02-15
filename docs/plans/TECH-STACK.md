# Tech Stack: Remote Cloud Code

**Версия:** MVP
**Язык:** Kotlin (везде)
**Дата:** 2025-02-14
**Версии обновлены:** 2025-02-14 (проверены на актуальность)

⚠️ **ВАЖНО:** Все обновления и откаты версий библиотек должны быть **согласованы с пользователем** перед использованием. Beta/RC версии допускаются только с явного согласия.

---

## 1. Architecture Overview

```
┌─────────────────────────────────────────────────────┐
│  Kotlin Multiplatform (Shared Business Logic)      │
│  - Models (ChatMessage, Session, API DTOs)         │
│  - Validation logic                                │
│  - Common utilities                                │
└──────────────┬──────────────────────────────────────┘
               │
    ┌──────────┴──────────┐
    │                     │
┌───▼────────────────┐  ┌─▼──────────────────────────┐
│  Backend (Ktor)    │  │  Frontend (Compose MP)     │
│  JVM Target        │  │  Android / Desktop / iOS   │
│  - Server logic    │  │  - UI Layer                │
│  - WebSocket       │  │  - API Client              │
│  - Database        │  │  - Local cache             │
└────────────────────┘  └────────────────────────────┘
```

---

## 2. Frontend Stack (Kotlin Multiplatform)

### Core Dependencies

```gradle
// Kotlin
org.jetbrains.kotlin:kotlin-stdlib
org.jetbrains.kotlin:kotlin-stdlib-common

// Coroutines (async programming)
org.jetbrains.kotlinx:kotlinx-coroutines-core
org.jetbrains.kotlinx:kotlinx-coroutines-android
org.jetbrains.kotlinx:kotlinx-coroutines-swing  // Desktop
```

### Networking

```gradle
// HTTP + WebSocket (Ktor Client)
io.ktor:ktor-client-core
io.ktor:ktor-client-websockets
io.ktor:ktor-client-okhttp              // Android engine
io.ktor:ktor-client-darwin              // iOS engine (native)
io.ktor:ktor-client-serialization-json
```

### Serialization

```gradle
// JSON (Kotlin native)
org.jetbrains.kotlinx:kotlinx-serialization-json
org.jetbrains.kotlinx:kotlinx-serialization-core
```

### Database (Local Cache)

```gradle
// SQLDelight (KMP SQLite)
com.squareup.sqldelight:runtime
com.squareup.sqldelight:android-driver      // Android
com.squareup.sqldelight:native-driver       // iOS
com.squareup.sqldelight:sqlite-driver       // Desktop
```

### UI Framework

```gradle
// Jetpack Compose Multiplatform (latest stable)
org.jetbrains.compose.ui:ui
org.jetbrains.compose.ui:ui-graphics
org.jetbrains.compose.ui:ui-text

// Material 3 UI (latest stable, standard components)
org.jetbrains.compose.material3:material3

org.jetbrains.compose.foundation:foundation
org.jetbrains.compose.runtime:runtime
org.jetbrains.compose.animation:animation

// Decompose for routing/navigation (multiplatform)
com.arkivanov.decompose:decompose
com.arkivanov.decompose:extensions-compose-jetbrains

// MVIKotlin for state management (MVI pattern, from Decompose author)
com.arkivanov.mvikotlin:mvikotlin                           // Core
com.arkivanov.mvikotlin:mvikotlin-main                      // Main store utilities
com.arkivanov.mvikotlin:mvikotlin-extensions-coroutines     // Coroutines integration
com.arkivanov.mvikotlin:mvikotlin-logging                   // Logging plugin
com.arkivanov.mvikotlin:mvikotlin-time-travel              // Time travel debugging

// Essenty for lifecycle management (from Decompose author)
com.arkivanov.essenty:lifecycle                             // Component lifecycle
com.arkivanov.essenty:instance-keeper                       // State preservation
```

### Local Settings

```gradle
// Key-value storage (token, session state)
com.russhwolf:multiplatform-settings
com.russhwolf:multiplatform-settings-serialization
```

### Logging

```gradle
// Multiplatform logging
io.github.aakira:napier
```

### Dependency Injection

```gradle
// Koin DI (production multiplatform) + Annotations with compiler plugin
io.insert-koin:koin-core
io.insert-koin:koin-compose              // Integration with Compose
io.insert-koin:koin-annotations          // Annotations (can be RC version)
io.insert-koin:koin-ksp                  // KSP compiler plugin for annotations
```

### Security

```gradle
// Android: Keystore for token encryption
androidx.security:security-crypto

// iOS: Keychain (via expect/actual)
// Desktop: Encrypted local storage (via expect/actual)
```

### Code Quality & Linting

```gradle
// Kotlin linter (code style checking)
org.jmailen.gradle:kotlinter-gradle

// Detekt (static analysis, code smells)
io.gitlab.arturbosch.detekt:detekt-gradle-plugin
io.gitlab.arturbosch.detekt:detekt-formatting
```

### Testing (Multiple Types)

```gradle
// Unit Testing
kotlin.test:kotlin-test
kotlin.test:kotlin-test-common
kotlin.test:kotlin-test-annotations-common

// Mocking
io.mockk:mockk
io.mockk:mockk-common

// Assertions & BDD style
io.kotest:kotest-assertions-core
io.kotest:kotest-framework-api
io.kotest:kotest-runner-junit5

// Integration testing
io.ktor:ktor-server-test-host          // Backend API testing

// UI testing (Android)
androidx.compose.ui:ui-test-manifest
androidx.compose.ui:ui-test-junit4

// Property-based testing
io.kotest:kotest-property               // For fuzz testing
```

---

## 3. Backend Stack (Ktor Server on JVM)

### Core Framework

```gradle
// Ktor Server
io.ktor:ktor-server-core
io.ktor:ktor-server-cio                 // HTTP engine (embeddable, lightweight)
io.ktor:ktor-server-websockets
io.ktor:ktor-serialization-kotlinx-json

// Content negotiation
io.ktor:ktor-server-content-negotiation

// CORS
io.ktor:ktor-server-cors
```

### Coroutines

```gradle
org.jetbrains.kotlinx:kotlinx-coroutines-core
org.jetbrains.kotlinx:kotlinx-coroutines-jdk8
```

### Database

```gradle
// SQLDelight (KMP, JDBC driver for JVM)
com.squareup.sqldelight:runtime
com.squareup.sqldelight:jdbc-driver
com.squareup.sqldelight:sqlite-driver
```

### Logging

```gradle
// Kotlin multiplatform logging
io.github.aakira:napier

// Alternative: Kotlin Logging
io.github.microutils:kotlin-logging

// SLF4J backend (if needed)
org.slf4j:slf4j-api
ch.qos.logback:logback-classic
```

### Dependency Injection

```gradle
// Koin
io.insert-koin:koin-core
io.insert-koin:koin-ktor
```

### Utilities

```gradle
// UUID generation
com.benasher44:uuid

// Environment variables
io.github.cdimascio:dotenv-java
```

### Testing

```gradle
// Testing framework
kotlin.test:kotlin-test-junit5

// Assertions
io.kotest:kotest-assertions-core
io.kotest:kotest-framework-api-jvm

// Mocking
io.mockk:mockk

// Server testing
io.ktor:ktor-server-test-host
```

---

## 4. Build System

### Gradle Configuration

```gradle
plugins {
    kotlin("multiplatform")
    kotlin("plugin.serialization")
    id("org.jetbrains.compose") version "1.6.0"
    id("com.google.devtools.ksp")
    id("com.android.application")        // Android
    id("com.android.library")            // For shared lib
}

kotlin {
    // Frontend targets
    android()
    jvm("desktop")
    iosArm64()
    iosSimulatorArm64()

    // Backend target (optional separate build)
    // jvm("backend")

    sourceSets {
        commonMain {
            dependencies {
                // Shared dependencies
            }
        }

        androidMain {
            dependencies {
                // Android-specific
            }
        }

        val desktopMain by getting {
            dependencies {
                // Desktop-specific
            }
        }

        val iosMain by creating {
            dependencies {
                // iOS-specific
            }
        }
    }
}
```

### Package Structure

```
remote-cloud-code/
├── shared/                          (KMP Shared Library)
│   ├── build.gradle.kts
│   ├── src/commonMain/
│   │   ├── kotlin/
│   │   │   ├── models/              (ChatMessage, Session, etc.)
│   │   │   ├── api/                 (API contracts, DTOs)
│   │   │   ├── validation/          (Shared validation logic)
│   │   │   └── util/                (Common utilities)
│   │   └── resources/
│   ├── src/androidMain/
│   ├── src/desktopMain/
│   └── src/iosMain/
│
├── backend/                         (Ktor Server, JVM)
│   ├── build.gradle.kts
│   ├── src/main/kotlin/
│   │   ├── Application.kt           (Main server)
│   │   ├── plugins/
│   │   │   ├── routing.kt
│   │   │   ├── websocket.kt
│   │   │   └── serialization.kt
│   │   ├── handlers/
│   │   │   ├── ChatHandler.kt
│   │   │   └── AuthHandler.kt
│   │   ├── database/
│   │   │   ├── Database.kt
│   │   │   └── schema.sq               (SQLDelight schema)
│   │   ├── models/
│   │   └── util/
│   ├── src/test/kotlin/
│   └── Dockerfile
│
├── app/                             (Compose Multiplatform)
│   ├── build.gradle.kts
│   ├── src/commonMain/
│   │   ├── kotlin/
│   │   │   ├── ui/screens/
│   │   │   ├── viewmodels/
│   │   │   ├── data/
│   │   │   │   ├── api/             (Ktor Client setup)
│   │   │   │   ├── repository/
│   │   │   │   └── cache/           (SQLDelight queries)
│   │   │   └── App.kt
│   │   └── resources/
│   ├── src/androidMain/
│   ├── src/desktopMain/
│   └── src/iosMain/
│
├── docker-compose.yml
├── settings.gradle.kts
└── build.gradle.kts
```

---

## 5. Database Schema (SQLDelight)

### Location: `backend/src/main/kotlin/database/schema.sq`

```sql
-- Chats table
CREATE TABLE IF NOT EXISTS chats (
    id TEXT PRIMARY KEY,
    session_id TEXT NOT NULL UNIQUE,
    created_at TEXT NOT NULL,
    last_activity TEXT NOT NULL,
    title TEXT DEFAULT "Chat"
);

-- Messages table
CREATE TABLE IF NOT EXISTS messages (
    id TEXT PRIMARY KEY,
    chat_id TEXT NOT NULL,
    role TEXT NOT NULL,
    content TEXT NOT NULL,
    timestamp TEXT NOT NULL,
    FOREIGN KEY(chat_id) REFERENCES chats(id) ON DELETE CASCADE
);

-- Indexes
CREATE INDEX IF NOT EXISTS idx_messages_chat_id ON messages(chat_id);
CREATE INDEX IF NOT EXISTS idx_messages_timestamp ON messages(timestamp);
```

---

## 6. Configuration Files

### Backend: `application.conf` (Ktor)

```hocon
ktor {
    deployment {
        port = 3000
        host = 0.0.0.0
    }
    application {
        modules = [ com.example.ApplicationKt.module ]
    }
}

server {
    authToken = ${?AUTH_TOKEN}
    gatewayPort = 3000
    dataDir = "./data"
}
```

### Environment Variables: `.env`

```bash
AUTH_TOKEN=your-unique-token-here
GATEWAY_PORT=3000
NODE_ENV=production
```

### Frontend: `local.properties`

```properties
sdk.dir=/path/to/android/sdk
org.gradle.jvmargs=-Xmx4096m
```

---

## 7. Dependency Versions (Lock File)

### `gradle/libs.versions.toml`

```toml
[versions]
# Latest Stable Versions (checked Feb 2025)
kotlin = "2.3.20-Beta2"              # Beta version (approved by user) - latest features
kotlinx-coroutines = "1.8.1"
ktor = "3.4.0"                       # Latest stable (major bump from 2.x)
compose = "1.10.1"                   # Latest stable (iOS now stable!)
sqldelight = "2.0.2"
koin = "3.5.6"                       # Latest LTS version
koin-annotations = "2.3.2"           # Latest stable
decompose = "2.2.2"                  # Latest stable
mvikotlin = "4.0.0"                  # Latest (released Jan 2025)
essenty = "1.3.0"                    # Latest (from Decompose author)
napier = "2.7.1"
kotest = "5.8.1"
mockk = "1.13.10"
detekt = "1.23.6"
kotlinter = "4.1.1"

[libraries]
# Kotlin
kotlin-stdlib = { module = "org.jetbrains.kotlin:kotlin-stdlib", version.ref = "kotlin" }
kotlinx-coroutines-core = { module = "org.jetbrains.kotlinx:kotlinx-coroutines-core", version.ref = "kotlinx-coroutines" }

# Ktor
ktor-server-core = { module = "io.ktor:ktor-server-core", version.ref = "ktor" }
ktor-client-core = { module = "io.ktor:ktor-client-core", version.ref = "ktor" }

# Compose
compose-ui = { module = "org.jetbrains.compose.ui:ui", version.ref = "compose" }

# SQLDelight
sqldelight-runtime = { module = "com.squareup.sqldelight:runtime", version.ref = "sqldelight" }

# Koin
koin-core = { module = "io.insert-koin:koin-core", version.ref = "koin" }

# Testing
kotlin-test = { module = "kotlin.test:kotlin-test", version.ref = "kotlin" }
mockk = { module = "io.mockk:mockk", version.ref = "mockk" }
kotest-assertions = { module = "io.kotest:kotest-assertions-core", version.ref = "kotest" }

[bundles]
ktor-server = ["ktor-server-core", "ktor-server-websockets"]
ktor-client = ["ktor-client-core", "ktor-client-websockets"]
```

---

## 8. Docker Deployment

### `Dockerfile`

```dockerfile
# Build stage
FROM gradle:8.4-jdk21 as builder
WORKDIR /app
COPY . .
RUN gradle build -x test

# Runtime stage
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=builder /app/backend/build/libs/*.jar app.jar
EXPOSE 3000
CMD ["java", "-jar", "app.jar"]
```

### `docker-compose.yml`

```yaml
version: '3.9'

services:
  gateway:
    build:
      context: .
      dockerfile: Dockerfile
    container_name: rcc-gateway
    ports:
      - "3000:3000"
    environment:
      - AUTH_TOKEN=${AUTH_TOKEN}
      - GATEWAY_PORT=3000
    volumes:
      - ./data:/app/data
      - ./logs:/app/logs
    restart: unless-stopped
    networks:
      - rcc-network

networks:
  rcc-network:
    driver: bridge
```

---

## 9. Code Quality & Linting Configuration

### KtLint Configuration (`.editorconfig`)

```ini
[*.{kt,kts}]
indent_size = 4
max_line_length = 120
ij_kotlin_allow_trailing_comma = true

# ktlint rules
ktlint_standard_no-wildcard-imports = enabled
ktlint_standard_filename = enabled
ktlint_standard_annotation = enabled
```

### Detekt Configuration (`detekt.yml`)

```yaml
build:
  maxIssues: 0
  excludeCorrectable: false

processors:
  active: true

console-reports:
  active: true

verify:
  detektProperties: detekt.properties

complexity:
  TooManyFunctions:
    threshold: 15

style:
  MaxLineLength:
    maxLineLength: 120
    ignoreComments: true
```

### Running Linters

```bash
# Format code (ktlint)
./gradlew ktlintFormat

# Check code quality (detekt)
./gradlew detekt

# Full checks before commit
./gradlew check ktlintCheck detekt test
```

---

## 10. Material 3 UI Standards & Responsive Design

### Material 3 Components (Standard Approach)

```kotlin
// Use Material 3 tokens and shapes
import androidx.compose.material3.*

// Themed colors (use M3 color system)
Surface(
    modifier = Modifier.fillMaxWidth(),
    color = MaterialTheme.colorScheme.surfaceContainer,
    shape = RoundedCornerShape(8.dp)
)

// Buttons (standard M3 variants)
Button(...)           // Filled button
OutlinedButton(...)   // Outlined
TextButton(...)       // Text button
FilledTonalButton(...) // Tonal

// Cards with M3 styling
Card(
    modifier = Modifier.padding(8.dp),
    shape = RoundedCornerShape(12.dp),
    colors = CardDefaults.cardColors(
        containerColor = MaterialTheme.colorScheme.surface
    )
)
```

### Responsive Design (Multiple Screen Sizes)

```kotlin
// Adaptive layout for phone, tablet, desktop
@Composable
fun AdaptiveLayout(
    modifier: Modifier = Modifier
) {
    val windowSizeClass = calculateWindowSizeClass()

    when (windowSizeClass.widthSizeClass) {
        WindowWidthSizeClass.Compact -> {
            // Phone layout (< 600dp)
            PhoneLayout(modifier)
        }
        WindowWidthSizeClass.Medium -> {
            // Tablet layout (600-840dp)
            TabletLayout(modifier)
        }
        WindowWidthSizeClass.Expanded -> {
            // Desktop layout (> 840dp)
            DesktopLayout(modifier)
        }
    }
}

// Helper: Calculate window size
fun calculateWindowSizeClass(): WindowSizeClass {
    return WindowSizeClass.calculateFromSize(
        DpSize(width = screenWidthDp, height = screenHeightDp)
    )
}
```

### Using Decompose for Navigation (Responsive)

```kotlin
// Decompose component tree structure
@Composable
fun RootContent(
    component: RootComponent
) {
    val windowSizeClass = calculateWindowSizeClass()

    when (windowSizeClass.widthSizeClass) {
        WindowWidthSizeClass.Compact -> {
            // Single stack navigation
            SinglePaneNavigation(component)
        }
        WindowWidthSizeClass.Expanded -> {
            // Dual pane layout
            Row {
                ChatListPane(component)
                ChatDetailPane(component)
            }
        }
    }
}
```

---

## 11. Development Workflow

### Running Backend Locally

```bash
# Terminal 1: Start Ktor server
cd backend
./gradlew run

# Server starts on http://localhost:3000
```

### Running Frontend (Android)

```bash
# Terminal 2: Start Android emulator
cd app
./gradlew :app:installDebug runDebugAndroidApp
```

### Running Frontend (Desktop)

```bash
# Terminal 2: Run desktop app
cd app
./gradlew :app:runDesktop
```

---

## 13. Testing Strategy (Multiple Test Types)

### Unit Tests

```kotlin
// Business logic, utilities, validation
class ChatValidationTest {
    @Test
    fun testValidateChatMessage() = runTest {
        val message = ChatMessage(...)
        val result = validateChatMessage(message)

        assertThat(result).isSuccess()
    }
}
```

### Integration Tests (Backend)

```kotlin
// API endpoints, WebSocket, Database
class ChatApiIntegrationTest {
    @Test
    fun testCreateChatEndpoint() = testApplication {
        val response = client.post("/api/chats") {
            contentType(ContentType.Application.Json)
            setBody(CreateChatRequest(...))
        }

        assertThat(response.status).isEqualTo(HttpStatusCode.Created)
    }
}
```

### Integration Tests (Frontend)

```kotlin
// API client, local cache, state management
class ChatRepositoryTest {
    @Test
    fun testLoadChatsFromCache() = runTest {
        // Setup: insert chats into SQLDelight
        database.insertChat(chatData)

        // Act
        val chats = repository.getChats()

        // Assert
        assertThat(chats).hasSize(1)
    }
}
```

### UI Tests (Compose)

```kotlin
// Screen rendering, user interactions
@Test
fun testChatListDisplay() {
    composeTestRule.setContent {
        ChatListScreen(...)
    }

    composeTestRule
        .onNodeWithText("Chat 1")
        .assertIsDisplayed()
        .performClick()

    composeTestRule
        .onNodeWithText("Chat detail")
        .assertIsDisplayed()
}
```

### E2E Tests (Backend + Frontend)

```kotlin
// Full flow: create chat → send command → receive output
@Test
fun testCreateChatAndSendCommand() = runTest {
    // 1. Create chat via API
    val chatId = apiClient.createChat()

    // 2. Connect WebSocket
    val websocket = WebSocketClient.connect(chatId)

    // 3. Send command
    apiClient.sendMessage(chatId, "npm install")

    // 4. Receive events
    val events = websocket.receiveEvents(timeout = 5.seconds)
    assertThat(events).contains(
        messageEvent(role = "assistant", content = "Building...")
    )
}
```

### Running Tests

```bash
# Unit + Integration tests (all)
./gradlew test

# Only unit tests
./gradlew test --tests "*.UnitTest"

# Only integration tests
./gradlew test --tests "*.IntegrationTest"

# UI tests (Android)
./gradlew connectedAndroidTest

# E2E tests
./gradlew e2eTest

# All tests with coverage
./gradlew testWithCoverage
```

### Code Coverage Targets

```
Target Coverage: 80%+
- Business logic: 90%+
- API endpoints: 85%+
- UI components: 70%+ (harder to test)
- Utilities: 95%+
```

---

## 11. IDE Setup

### IntelliJ IDEA / Android Studio

```
File → Project Structure → SDKs
- Kotlin: 1.9.20
- Java: 21
- Android SDK: API 34+

Plugins (recommended):
- Kotlin
- Gradle
- SQLDelight
- Compose
```

---

## 12. Summary Table (Актуальные версии Feb 2025)

| Layer | Technology | Version | Статус | Комментарий |
|-------|-----------|---------|--------|-----------|
| **Language** | Kotlin | 2.3.20-Beta2 | 🔵 Beta (approved) | Latest features, K2 compiler ready |
| **Backend** | Ktor Server | 3.4.0 | ✅ Stable | Major update, OpenAPI generation |
| **Frontend** | Compose MP | 1.10.1 | ✅ Stable | iOS теперь stable! |
| **Navigation** | Decompose | 2.2.2 | ✅ Stable | Multiplatform routing |
| **Networking** | Ktor Client | 3.4.0 | ✅ Stable | Duplex streaming support |
| **Database** | SQLDelight | 2.0.2 | ✅ Stable | Type-safe SQL |
| **State** | Coroutines + Flow | 1.8.1 | ✅ Stable | Latest stable |
| **DI** | Koin + Annotations | 3.5.6 + 2.3.2 | ✅ Stable | Compiler plugin KSP 2.3.2 |
| **JSON** | Kotlinx Serialization | - | ✅ Stable | Bundled with Kotlin |
| **Logging** | Napier | 2.7.1 | ✅ Stable | KMP standard |
| **Code Quality** | ktlint + Detekt | 4.1.1 + 1.23.6 | ✅ Stable | Automated checks |
| **Testing** | Kotest + Mockk | 5.8.1 + 1.13.10 | ✅ Stable | Latest stable versions |
| **State Management** | MVIKotlin | 4.0.0 | ✅ Stable | MVI pattern, time travel debugging |
| **Component Lifecycle** | Essenty | 1.3.0 | ✅ Stable | From Decompose author |
| **Build** | Gradle | 8.6+ | ✅ Stable | Latest JVM toolchain |
| **Containerization** | Docker | latest | ✅ Stable | Standard |

---

## 14. Version Management & Update Process

### Checking for Updates

```bash
# Check for available updates
./gradlew dependencyUpdates

# Show output in HTML report
./gradlew dependencyUpdates -x test
# Open: build/reports/dependencyUpdates/report.html
```

### Version Update Workflow

1. **Detect Update** → Agent находит новую версию библиотеки
2. **Propose to User** → Agent предлагает обновление с информацией:
   - Текущая версия vs новая версия
   - Тип: Stable / Beta / RC
   - Changelog highlights
   - Potential breaking changes
3. **Get Approval** → User согласует или отклоняет
4. **Apply Update** → Agent обновляет версию в `gradle/libs.versions.toml`
5. **Test** → Запускаются тесты для проверки совместимости
6. **Commit** → Коммит с сообщением о обновлении

### Version Stability Guidelines

| Статус | Когда использовать | Требует согласия |
|--------|-------------------|------------------|
| **Stable** | Production, MVP | ❌ Нет (стандартное обновление) |
| **Beta** | Features/improvements нужны | ✅ **Да, обязательно** |
| **RC** | Bug fixes, нужно urgently | ✅ **Да, обязательно** |
| **Alpha/Preview** | Experimental | ✅ **Да, обязательно** |

### Backward Compatibility

- ✅ **Patch updates** (1.0.0 → 1.0.1) → Auto-upgrade, no approval needed
- ⚠️ **Minor updates** (1.0.0 → 1.1.0) → Propose but should be safe
- ❌ **Major updates** (1.0.0 → 2.0.0) → **Always ask user**, test thoroughly

### Example: Version Update Request

```
🔄 Version Update Available

Library: Compose Multiplatform
Current: 1.9.0 (stable)
Available: 1.10.0 (stable)

Changes:
- iOS performance improvements
- New Material 3 components
- Bug fixes for WebSocket

Impact: Minor (no breaking changes)
Tests: ✅ All pass locally

Proceed with update? (Y/N)
```

---

## 15. Key Implementation Requirements

### Version Management
- ✅ Use **latest stable versions** for all dependencies
- ✅ Check for updates monthly: `./gradlew dependencyUpdates`
- ✅ Pin versions in `gradle/libs.versions.toml` for reproducibility
- ✅ Can use RC/beta versions for cutting-edge features (like Koin Annotations)

### Code Quality Standards
- ✅ **ktlint** — automatic code formatting (run before every commit)
- ✅ **detekt** — static analysis, catch code smells early
- ✅ **CI/CD** — enforce checks: `./gradlew check` must pass before merge
- ✅ **Pre-commit hooks** — automated formatting + linting

### UI/UX Standards
- ✅ **Material 3 only** — use standard M3 components, colors, shapes
- ✅ **Decompose navigation** — all routing through Decompose components
- ✅ **Responsive design** — test on compact (phone), medium (tablet), expanded (desktop)
- ✅ **Window size class** — adapt layouts based on `WindowSizeClass`
- ✅ **Accessibility** — semantics modifiers, content descriptions

### Testing Requirements
- ✅ **Unit tests** — all business logic, utilities, validation (90%+ coverage)
- ✅ **Integration tests** — API endpoints, database, state management
- ✅ **UI tests** — screen rendering, user interactions
- ✅ **E2E tests** — full flow scenarios (create chat, send command, etc.)
- ✅ **Coverage target** — 80%+ overall, higher for critical paths

---

## 15. Advantages of This Stack

✅ **Single Language:** Kotlin везде (backend + frontend + shared)
✅ **Type Safety:** Compile-time checks, no runtime surprises
✅ **Agent-Friendly:** Consistent patterns, easy for AI to work with
✅ **Shared Code:** Models, validation, business logic в одной KMP lib
✅ **Performance:** Ktor быстро, Compose оптимизирован, SQLDelight efficient
✅ **Production-Proven:** All libraries used in production by large companies
✅ **Future-Proof:** Легко добавить iOS, Desktop, или новые features

---

**End of Tech Stack Document**
