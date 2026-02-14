# Architecture Guide: Clean, Scalable, Production-Grade

**Version:** MVP
**Philosophy:** SOLID principles, Clean Architecture, Feature-based structure
**Scalability:** Designed to grow from MVP to enterprise

---

## 1. Overall Architecture Principles

### Core Principles

✅ **SOLID Principles**
- **S**ingle Responsibility — каждый class одну ответственность
- **O**pen/Closed — open for extension, closed for modification
- **L**iskov Substitution — interfaces заменяемы
- **I**nterface Segregation — narrow, specific interfaces
- **D**ependency Inversion — depend on abstractions, not implementations

✅ **Feature-Based Structure** (не по слоям!)
- Каждая feature self-contained
- Легко добавлять/удалять features
- Минимальные кросс-feature dependencies

✅ **Separation of Concerns**
- UI layer ничего не знает о DB
- Domain layer независим от frameworks
- Data layer — только implementation details

✅ **Testability First**
- All code testable in isolation
- Mock-friendly interfaces
- No god objects

---

## 2. Shared Library Architecture (KMP)

### Directory Structure

```
shared/src/commonMain/kotlin/
├── domain/                                 # Business logic (NO dependencies on frameworks!)
│   ├── entity/                            # Core business models
│   │   ├── Chat.kt
│   │   ├── Message.kt
│   │   ├── User.kt
│   │   └── Session.kt
│   │
│   ├── repository/                        # Repository interfaces (contracts)
│   │   ├── ChatRepository.kt              # interface only
│   │   ├── MessageRepository.kt
│   │   └── SessionRepository.kt
│   │
│   ├── usecase/                           # Use cases (application logic)
│   │   ├── chat/
│   │   │   ├── GetChatsUseCase.kt
│   │   │   ├── CreateChatUseCase.kt
│   │   │   ├── DeleteChatUseCase.kt
│   │   │   └── SendMessageUseCase.kt
│   │   ├── auth/
│   │   │   ├── AuthenticateUseCase.kt
│   │   │   └── RefreshTokenUseCase.kt
│   │   └── session/
│   │       └── ManageSessionUseCase.kt
│   │
│   ├── error/                             # Domain-level errors
│   │   ├── DomainError.kt                 # sealed class for all domain errors
│   │   ├── ChatError.kt
│   │   ├── AuthError.kt
│   │   └── NetworkError.kt
│   │
│   └── validator/                         # Business validation rules
│       ├── ChatValidator.kt
│       ├── MessageValidator.kt
│       └── CredentialValidator.kt
│
├── data/                                   # Data layer (implementations, external frameworks)
│   ├── repository/                        # Repository implementations
│   │   ├── ChatRepositoryImpl.kt           # implements ChatRepository
│   │   ├── MessageRepositoryImpl.kt
│   │   └── SessionRepositoryImpl.kt
│   │
│   ├── datasource/                        # Data sources (API, DB, cache)
│   │   ├── remote/
│   │   │   ├── ChatApiDataSource.kt       # Ktor client calls
│   │   │   └── ApiClient.kt               # Ktor setup
│   │   ├── local/
│   │   │   ├── ChatCacheDataSource.kt     # SQLDelight queries
│   │   │   └── LocalCache.kt
│   │   └── websocket/
│   │       └── WebSocketDataSource.kt     # WebSocket events
│   │
│   ├── dto/                               # API response models
│   │   ├── ChatDto.kt
│   │   ├── MessageDto.kt
│   │   └── UserDto.kt
│   │
│   ├── mapper/                            # DTO ↔ Domain entity mapping
│   │   ├── ChatMapper.kt                  # Dto → Entity (clean boundary)
│   │   └── MessageMapper.kt
│   │
│   └── db/                                # SQLDelight setup
│       ├── schema.sq                      # SQLDelight schema
│       └── DatabaseFactory.kt
│
└── di/                                     # Dependency Injection (Koin config)
    ├── DomainModule.kt                    # Use cases, validators
    ├── DataModule.kt                      # Repositories, data sources
    └── PresentationModule.kt              # (for backend: handlers, services)
```

---

## 3. Backend Architecture (Ktor Server)

### Feature-Based Backend Structure

```
backend/src/main/kotlin/
├── features/                              # Feature modules (self-contained)
│   ├── chat/
│   │   ├── handler/
│   │   │   └── ChatHandler.kt             # HTTP handlers for chat endpoints
│   │   ├── service/
│   │   │   └── ChatService.kt             # Business logic (use repository)
│   │   ├── dto/
│   │   │   ├── CreateChatRequest.kt
│   │   │   ├── ChatResponse.kt
│   │   │   └── MessageResponse.kt
│   │   ├── route/
│   │   │   └── ChatRoutes.kt              # Route definitions
│   │   └── ChatFeature.kt                 # Feature entry point
│   │
│   ├── auth/
│   │   ├── handler/
│   │   │   └── AuthHandler.kt
│   │   ├── service/
│   │   │   └── AuthService.kt
│   │   ├── dto/
│   │   │   └── AuthRequest.kt
│   │   ├── route/
│   │   │   └── AuthRoutes.kt
│   │   └── AuthFeature.kt
│   │
│   └── websocket/
│       ├── handler/
│       │   └── WebSocketHandler.kt
│       ├── service/
│       │   └── WebSocketService.kt
│       └── WebSocketFeature.kt
│
├── plugins/                               # Ktor plugins (reusable configuration)
│   ├── routing.kt                         # Main routing setup
│   ├── websockets.kt                      # WebSocket plugin config
│   ├── serialization.kt                   # JSON serialization setup
│   ├── monitoring.kt                      # Logging, metrics
│   ├── security.kt                        # Authentication, authorization
│   └── cors.kt                            # CORS setup
│
├── infrastructure/                        # Infrastructure layer
│   ├── config/
│   │   ├── AppConfig.kt                   # Configuration management
│   │   └── EnvironmentConfig.kt           # Env variables
│   ├── persistence/
│   │   ├── Database.kt                    # DB initialization
│   │   └── Migration.kt                   # DB migrations
│   ├── api/
│   │   ├── ApiClient.kt                   # HTTP client setup
│   │   └── WebSocketClient.kt
│   ├── error/
│   │   ├── ErrorHandler.kt                # Global error handling
│   │   └── HttpExceptionMapper.kt         # Map domain errors to HTTP
│   └── logging/
│       └── LoggerSetup.kt
│
├── shared/                                # Use shared KMP domain layer
│   └── (references to shared/ package)
│
├── di/                                    # Dependency Injection
│   ├── KoinSetup.kt
│   ├── RepositoryModule.kt
│   ├── ServiceModule.kt
│   └── HandlerModule.kt
│
└── Application.kt                         # Entry point
```

### Backend Layered Architecture

```
┌────────────────────────────────────┐
│   HTTP Layer (Ktor Routes)         │
│   - ChatRoutes, AuthRoutes         │
└────────────┬───────────────────────┘
             │
┌────────────▼───────────────────────┐
│   Handler Layer                    │
│   - ChatHandler (receives requests)│
│   - Validates HTTP input           │
│   - Delegates to service           │
└────────────┬───────────────────────┘
             │
┌────────────▼───────────────────────┐
│   Service Layer (Business Logic)   │
│   - ChatService                    │
│   - Orchestrates use cases         │
│   - Domain error handling          │
└────────────┬───────────────────────┘
             │
┌────────────▼───────────────────────┐
│   Use Case Layer (shared domain)   │
│   - GetChatsUseCase                │
│   - CreateChatUseCase              │
│   - Pure business logic            │
└────────────┬───────────────────────┘
             │
┌────────────▼───────────────────────┐
│   Repository Layer                 │
│   - ChatRepository (interface)     │
│   - ChatRepositoryImpl              │
│   - Abstract data source           │
└────────────┬───────────────────────┘
             │
┌────────────▼───────────────────────┐
│   Data Source Layer                │
│   - Remote (Ktor client calls)     │
│   - Local (SQLDelight)             │
│   - WebSocket (events)             │
└────────────────────────────────────┘
```

### Example: Chat Feature Implementation

```kotlin
// ============ Domain Layer (shared) ============

// Entity
data class Chat(
    val id: String,
    val sessionId: String,
    val createdAt: LocalDateTime,
    val messages: List<Message> = emptyList()
)

// Repository Interface (contract)
interface ChatRepository {
    suspend fun getChats(): Result<List<Chat>>
    suspend fun createChat(): Result<Chat>
    suspend fun deleteChat(id: String): Result<Unit>
}

// Use Case
class GetChatsUseCase(
    private val repository: ChatRepository,
    private val validator: ChatValidator
) {
    suspend operator fun invoke(): Result<List<Chat>> {
        return try {
            repository.getChats()
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

// ============ Data Layer ============

// DTO (API response)
@Serializable
data class ChatDto(
    val id: String,
    val sessionId: String,
    val createdAt: String,
    val messages: List<MessageDto> = emptyList()
)

// Mapper
object ChatMapper {
    fun toDomain(dto: ChatDto): Chat = Chat(
        id = dto.id,
        sessionId = dto.sessionId,
        createdAt = LocalDateTime.parse(dto.createdAt),
        messages = dto.messages.map { MessageMapper.toDomain(it) }
    )

    fun toDto(domain: Chat): ChatDto = ChatDto(
        id = domain.id,
        sessionId = domain.sessionId,
        createdAt = domain.createdAt.toString(),
        messages = domain.messages.map { MessageMapper.toDto(it) }
    )
}

// Repository Implementation
class ChatRepositoryImpl(
    private val remoteDataSource: ChatApiDataSource,
    private val localDataSource: ChatCacheDataSource
) : ChatRepository {

    override suspend fun getChats(): Result<List<Chat>> = try {
        val dtos = remoteDataSource.getChats()
        val chats = dtos.map { ChatMapper.toDomain(it) }

        // Cache locally
        chats.forEach { localDataSource.saveChat(it) }

        Result.success(chats)
    } catch (e: Exception) {
        Result.failure(e)
    }
}

// ============ Backend Handler Layer ============

// Handler (receives HTTP requests)
class ChatHandler(
    private val chatService: ChatService
) {
    suspend fun getChats(call: ApplicationCall) {
        val result = chatService.getChats()
        result.onSuccess { chats ->
            call.respond(HttpStatusCode.OK, chats.map { toChatResponse(it) })
        }.onFailure { error ->
            handleError(call, error)
        }
    }

    suspend fun createChat(call: ApplicationCall) {
        val request = call.receive<CreateChatRequest>()
        val result = chatService.createChat(request)
        result.onSuccess { chat ->
            call.respond(HttpStatusCode.Created, toChatResponse(chat))
        }.onFailure { error ->
            handleError(call, error)
        }
    }

    private suspend fun handleError(call: ApplicationCall, error: Throwable) {
        val httpStatus = when (error) {
            is ChatError.NotFound -> HttpStatusCode.NotFound
            is AuthError.Unauthorized -> HttpStatusCode.Unauthorized
            else -> HttpStatusCode.InternalServerError
        }
        call.respond(httpStatus, ErrorResponse(error.message ?: "Unknown error"))
    }
}

// Service (orchestrates use cases)
class ChatService(
    private val getChatsUseCase: GetChatsUseCase,
    private val createChatUseCase: CreateChatUseCase,
    private val validator: ChatValidator
) {
    suspend fun getChats(): Result<List<Chat>> {
        return getChatsUseCase()
    }

    suspend fun createChat(request: CreateChatRequest): Result<Chat> {
        // Validate
        validator.validateCreateRequest(request)
            .onFailure { return Result.failure(it) }

        return createChatUseCase(request.toEntity())
    }
}

// Routes
fun Route.chatRoutes(handler: ChatHandler) {
    route("/api/chats") {
        get {
            handler.getChats(call)
        }
        post {
            handler.createChat(call)
        }
    }
}
```

---

## 4. Client Architecture (Android/Multiplatform)

### Feature-Based Client Structure

```
app/src/commonMain/kotlin/
├── features/                              # Feature modules
│   ├── chatlist/
│   │   ├── component/
│   │   │   ├── ChatListComponent.kt       # Decompose component
│   │   │   └── ChatListComponentImpl.kt
│   │   ├── store/
│   │   │   ├── ChatListStore.kt           # MVIKotlin store interface
│   │   │   ├── ChatListStoreFactory.kt
│   │   │   ├── ChatListIntent.kt
│   │   │   ├── ChatListState.kt
│   │   │   └── ChatListAction.kt
│   │   ├── ui/
│   │   │   ├── ChatListScreen.kt
│   │   │   ├── ChatListContent.kt
│   │   │   └── ChatItem.kt
│   │   └── di/
│   │       └── ChatListModule.kt
│   │
│   ├── chatdetail/
│   │   ├── component/
│   │   ├── store/
│   │   ├── ui/
│   │   └── di/
│   │
│   └── settings/
│       ├── component/
│       ├── store/
│       ├── ui/
│       └── di/
│
├── root/                                  # App root component
│   ├── RootComponent.kt
│   └── RootComponentImpl.kt
│
├── shared/                                # References to KMP domain
│   └── (repositories, use cases from shared/)
│
├── theme/                                 # Material 3 theme
│   ├── Theme.kt
│   ├── Color.kt
│   ├── Typography.kt
│   └── Shape.kt
│
├── di/                                    # Koin setup
│   ├── KoinSetup.kt
│   ├── RepositoryModule.kt
│   ├── PresentationModule.kt
│   └── DomainModule.kt
│
└── App.kt                                 # App entry point
```

---

## 5. Error Handling Strategy

### Hierarchical Error Types

```kotlin
// Domain layer - pure business errors
sealed class DomainError : Exception() {
    abstract val message: String
}

sealed class ChatError : DomainError() {
    data class NotFound(override val message: String = "Chat not found") : ChatError()
    data class InvalidInput(override val message: String) : ChatError()
    data class OperationFailed(override val message: String) : ChatError()
}

sealed class AuthError : DomainError() {
    data class Unauthorized(override val message: String = "Unauthorized") : AuthError()
    data class TokenExpired(override val message: String = "Token expired") : AuthError()
}

sealed class NetworkError : DomainError() {
    data class Timeout(override val message: String = "Request timeout") : NetworkError()
    data class ConnectionFailed(override val message: String) : NetworkError()
}

// Backend - map to HTTP
fun DomainError.toHttpStatus(): HttpStatusCode = when (this) {
    is ChatError.NotFound -> HttpStatusCode.NotFound
    is ChatError.InvalidInput -> HttpStatusCode.BadRequest
    is AuthError.Unauthorized -> HttpStatusCode.Unauthorized
    is NetworkError.Timeout -> HttpStatusCode.GatewayTimeout
    else -> HttpStatusCode.InternalServerError
}

// Frontend - map to UI
fun DomainError.toUserMessage(): String = when (this) {
    is ChatError.NotFound -> "Chat not found"
    is ChatError.InvalidInput -> "Invalid input: ${this.message}"
    is AuthError.Unauthorized -> "Please login again"
    is NetworkError.Timeout -> "Connection timed out, please retry"
    else -> "Something went wrong"
}
```

---

## 6. Testing Strategy by Layer

### Domain Layer Tests (NO framework dependencies!)

```kotlin
@Test
fun testGetChatsReturnsChats() = runTest {
    // Pure business logic, no mocking frameworks needed
    val useCase = GetChatsUseCase(mockRepository, mockValidator)
    val result = useCase()

    assertTrue(result.isSuccess)
    assertEquals(3, result.getOrNull()?.size)
}
```

### Data Layer Tests (Repository logic)

```kotlin
@Test
fun testChatRepositoryFallsBackToCache() = runTest {
    // Remote fails, should use local cache
    val repo = ChatRepositoryImpl(
        remoteDataSource = mockRemoteThatFails(),
        localDataSource = mockLocalWithData()
    )

    val result = repo.getChats()
    assertTrue(result.isSuccess)
}
```

### Backend Feature Tests (Handler + Service)

```kotlin
@Test
fun testGetChatsEndpoint() = testApplication {
    val response = client.get("/api/chats") {
        header("Authorization", "Bearer token")
    }

    assertEquals(HttpStatusCode.OK, response.status)
    val chats = response.body<List<ChatResponse>>()
    assertEquals(3, chats.size)
}
```

### Frontend Store Tests (MVIKotlin)

```kotlin
@Test
fun testChatListStoreLoadsChats() = runTest {
    val store = ChatListStoreFactory(mockRepository, storeFactory).create(context)
    store.accept(ChatListIntent.LoadChats)

    advanceUntilIdle()
    assertThat(store.state.chats).isNotEmpty()
}
```

---

## 7. MVP vs V2: What We Defer

### ✅ MVP Includes

```
Backend:
- Clean architecture (domain, data, handler layers)
- Feature-based organization
- Repository pattern
- Error handling
- Basic logging
- SQLDelight for storage
- Ktor server

Frontend:
- Decompose + MVIKotlin
- Feature-based (chatlist, chatdetail)
- Material 3 UI
- SQLDelight local cache
- Basic error handling

Shared:
- Domain entities
- Repository interfaces
- Use cases
- DTOs + mappers
```

### 🔵 V2+ Features (Defer for Now)

```
Backend:
❌ Advanced caching (Redis)
❌ Rate limiting (advanced)
❌ API versioning (v1, v2)
❌ GraphQL
❌ Advanced monitoring/metrics
❌ Multi-tenancy
❌ Search/filtering (advanced)

Frontend:
❌ Offline-first (offline editing)
❌ End-to-end encryption
❌ Advanced animations
❌ A/B testing framework
❌ Advanced analytics
❌ Multiple VPS support

Infrastructure:
❌ Kubernetes
❌ Advanced CI/CD
❌ Blue-green deployments
❌ CDN
❌ Database replication
```

---

## 8. Code Organization Principles

### Module Dependencies (Clean Architecture)

```
Dependency Flow (→ means depends on):

UI Layer (Compose) → Presentation Layer (MVIKotlin Store)
Presentation Layer → Domain Layer (Use Cases, Entities)
Domain Layer → (nothing, pure business logic)
Data Layer → Domain Layer (implements interfaces)
Handlers → Service → Domain Layer
Service → Use Cases → Repositories

KEY: Domain layer has NO dependencies on outer layers!
```

### Feature Organization

```
✅ GOOD: Feature-based
app/
├── features/chatlist/   (self-contained, can be moved/removed)
├── features/chatdetail/
├── shared/domain/       (shared between features)
└── di/                  (wires everything)

❌ BAD: Layer-based (creates tight coupling)
app/
├── ui/     (all UI together)
├── data/   (all data together)
└── logic/  (all logic together)
```

---

## 9. Dependency Injection Configuration

### Koin Setup (Explicit, Testable)

```kotlin
// di/KoinSetup.kt
fun setupKoin() {
    startKoin {
        modules(
            domainModule,
            dataModule,
            presentationModule
        )
    }
}

// di/DomainModule.kt
val domainModule = module {
    // Use cases (depends on repositories)
    factory { GetChatsUseCase(get(), get()) }
    factory { CreateChatUseCase(get()) }

    // Validators
    factory { ChatValidator() }
    factory { MessageValidator() }
}

// di/DataModule.kt
val dataModule = module {
    // Repositories (depends on data sources)
    single<ChatRepository> {
        ChatRepositoryImpl(get(), get())
    }

    // Data sources
    single { ChatApiDataSource(get()) }
    single { ChatCacheDataSource(get()) }
    single { ApiClient() }
}

// di/PresentationModule.kt (Frontend)
val presentationModule = module {
    factory<StoreFactory> { LoggingStoreFactory(SimpleStoreFactory()) }

    // Component factories
    factory { (context: ComponentContext) ->
        ChatListComponentImpl(context, get(), get(), {})
    }
}
```

---

## 10. Documentation & Standards

### Code Patterns Every Feature Should Follow

1. **Feature naming:** `{Feature}Component`, `{Feature}Store`, `{Feature}Screen`, `{Feature}Service`
2. **Repository pattern:** Always interface + impl
3. **Use cases:** One responsibility per use case
4. **Error handling:** Use sealed class hierarchies
5. **Testing:** Test each layer independently
6. **Logging:** Use Napier logger (KMP compatible)

### Architecture Review Checklist

- [ ] Feature is self-contained (minimal external dependencies)
- [ ] Repository interface defined before implementation
- [ ] Use cases have single responsibility
- [ ] Error handling uses domain error types
- [ ] Components are testable in isolation
- [ ] No framework code in domain layer
- [ ] Dependency injection via Koin
- [ ] Code follows SOLID principles
- [ ] Tests cover happy path + error cases
- [ ] Feature can be removed without breaking others

---

**End of Architecture Guide**

Этот документ — blueprint для scalable, production-grade приложения.
Каждый архитектор будет доволен разложением! 🎯
