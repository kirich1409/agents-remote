# Test Coverage Improvement Design

**Date:** 2026-02-16
**Status:** Approved
**Approach:** Hybrid (Unit + Integration Tests)

---

## 1. Overview

### Current State
- ✅ Backend: 26 tests (ClaudeCodeService, ChatHandler, WebSocketHandler, Integration)
- ⚠️ Shared/Domain: Partial coverage (GetChatsUseCase, ChatRepository, Chat entity only)
- ⚠️ App: Minimal coverage (MVI Stores only)

### Critical Gaps
1. **CreateChatUseCase** - chat creation business logic
2. **SendMessageUseCase** - message sending (most critical)
3. **ChatMapper** - DTO↔Domain mapping
4. **Message entity** - domain model
5. **RccApiClient** - HTTP client
6. **AppChatRepositoryImpl** - client repository

### Goals
- **Primary focus:** Business logic (Use Cases)
- **Coverage target:** 80%+ for Use Cases
- **Strategy:** Hybrid - Unit tests for edge cases, Integration tests for critical flows

---

## 2. Architecture

### Test Structure
```
shared/src/commonTest/kotlin/com/example/rcc/
├── domain/
│   ├── usecase/
│   │   ├── CreateChatUseCaseTest.kt      # Unit tests (mocked)
│   │   ├── SendMessageUseCaseTest.kt     # Unit tests (mocked)
│   │   └── integration/
│   │       ├── ChatFlowIntegrationTest.kt  # Integration: Create + Send flow
│   │       └── MessageFlowIntegrationTest.kt
│   └── entity/
│       └── MessageTest.kt                # Unit tests for Message entity
├── data/
│   └── mapper/
│       └── ChatMapperTest.kt             # Unit tests for mapping

app/src/commonTest/kotlin/com/example/rcc/
├── data/
│   ├── api/
│   │   └── RccApiClientTest.kt           # Unit tests with mock HTTP
│   └── repository/
│       └── AppChatRepositoryImplTest.kt  # Unit tests with mock API
```

### Principles
- **Unit tests** in same package structure as production code
- **Integration tests** in separate `integration/` folder for clarity
- Follow existing patterns (as in backend)
- Each Test class tests one production class

### Tools
- `kotlin-test` (already in use)
- `mockk` (already in dependencies)
- `kotlinx-coroutines-test` (for suspend functions)
- `kotest-assertions` (for better assertions)

---

## 3. Test Components

### CreateChatUseCase - Unit Tests (~15-20 tests)
- ✅ Successful chat creation with valid sessionId
- ✅ SessionId validation (empty, null, invalid UUID)
- ❌ Repository failure handling
- ❌ Duplicate sessionId handling
- ⚡ Thread safety (concurrent creation)

**Test categories:**
1. Happy path scenarios
2. Input validation
3. Error handling
4. Concurrency edge cases

### SendMessageUseCase - Unit Tests (~20-25 tests)
- ✅ Successful message sending
- ✅ ChatId validation (empty, null, non-existent)
- ✅ Content validation (empty, null, too long)
- ❌ Chat not found
- ❌ Repository failure
- ⚡ Concurrent sends to same chat

**Test categories:**
1. Happy path scenarios
2. Input validation (chatId, content)
3. Error handling (not found, failures)
4. Concurrency edge cases

### ChatMapper - Unit Tests (~10-15 tests)
- DTO → Entity mapping (all fields)
- Entity → DTO mapping
- Null handling
- Invalid data handling
- Timestamp conversions

### Message Entity - Unit Tests (~8-10 tests)
- Creation with valid data
- Field validation
- Role enum handling
- Timestamp utilities

### RccApiClient - Unit Tests (~10-12 tests)
- HTTP request/response handling
- Error handling (network, HTTP errors)
- Serialization/deserialization
- Timeout handling

### AppChatRepositoryImpl - Unit Tests (~8-10 tests)
- CRUD operations
- API client integration
- Error propagation
- Data transformation

### Integration Tests (~15-20 tests)
- Full flow: CreateChat → SendMessage
- Error scenarios end-to-end
- Repository integration with real in-memory implementations

---

## 4. Testing Strategy

### Test Distribution
- **Unit tests (70%):** Fast, isolated, mocked dependencies, focus on edge cases
- **Integration tests (30%):** Real repositories (in-memory), critical happy paths, end-to-end flows

### Coverage Metrics
- **Target:** 80%+ code coverage for Use Cases
- **Metrics:** Branch coverage + line coverage
- **CI:** Tests run on every PR

### Test Pyramid
```
        /\
       /  \  E2E (existing: ChatApiIntegrationTest)
      /____\
     /      \
    / Integration \ (new: 15-20 tests)
   /____________\
  /              \
 /   Unit Tests   \ (new: 50-60 tests)
/__________________\
```

---

## 5. Implementation Plan

### Phase 1: Use Cases Unit Tests (Priority 1)
1. **CreateChatUseCaseTest** (~15-20 tests)
2. **SendMessageUseCaseTest** (~20-25 tests)
3. **ChatMapperTest** (~10-15 tests) - needed for Use Cases
4. **MessageTest** (~8-10 tests)

**Deliverable:** Core business logic fully covered

### Phase 2: App Layer Tests (Priority 2)
1. **RccApiClientTest** (~10-12 tests)
2. **AppChatRepositoryImplTest** (~8-10 tests)

**Deliverable:** Client-side data layer covered

### Phase 3: Integration Tests (Priority 3)
1. **ChatFlowIntegrationTest** (~8-10 tests)
2. **MessageFlowIntegrationTest** (~7-10 tests)

**Deliverable:** Critical flows validated end-to-end

---

## 6. Expected Outcomes

### Metrics
- **Total new tests:** ~70-80
- **Current coverage:** ~30-40% (estimated)
- **Target coverage:** 80%+ for Use Cases
- **Estimated effort:** 6-8 hours

### Benefits
- ✅ Confidence in business logic correctness
- ✅ Fast feedback loop (unit tests)
- ✅ Regression protection
- ✅ Documentation through tests
- ✅ Easier refactoring

### Success Criteria
1. All Use Cases have comprehensive unit tests
2. Integration tests cover critical flows
3. CI pipeline green with all tests passing
4. Code coverage report shows 80%+ for Use Cases
5. No flaky tests

---

## 7. Technical Details

### Mock Strategy
```kotlin
// Example: CreateChatUseCaseTest
class CreateChatUseCaseTest {
    private val chatRepository = mockk<ChatRepository>()
    private val useCase = CreateChatUseCase(chatRepository)

    @Test
    fun `should create chat with valid sessionId`() = runTest {
        // Given
        val sessionId = "valid-uuid"
        val expectedChat = Chat(...)
        coEvery { chatRepository.createChat(sessionId) } returns Result.success(expectedChat)

        // When
        val result = useCase(sessionId)

        // Then
        result.isSuccess shouldBe true
        result.getOrNull() shouldBe expectedChat
        coVerify { chatRepository.createChat(sessionId) }
    }
}
```

### Integration Test Strategy
```kotlin
// Example: ChatFlowIntegrationTest
class ChatFlowIntegrationTest {
    private val repository = ChatRepositoryImpl() // Real in-memory implementation
    private val createChatUseCase = CreateChatUseCase(repository)
    private val sendMessageUseCase = SendMessageUseCase(repository)

    @Test
    fun `should create chat and send message successfully`() = runTest {
        // Given
        val sessionId = UUID.randomUUID().toString()

        // When - Create chat
        val createResult = createChatUseCase(sessionId)
        val chat = createResult.getOrThrow()

        // Then - Chat created
        chat.sessionId shouldBe sessionId

        // When - Send message
        val messageResult = sendMessageUseCase(chat.id, "Test message")

        // Then - Message sent
        messageResult.isSuccess shouldBe true
    }
}
```

---

## 8. Maintenance Plan

### CI Integration
- Tests run on every commit
- Branch protection requires all tests passing
- Coverage reports in PR comments

### Test Hygiene
- No flaky tests allowed
- Clear test names (behavior-driven)
- Minimal test data setup
- Isolated tests (no shared state)

### Documentation
- Each test class has overview comment
- Complex scenarios have inline comments
- README in test folders if needed

---

## Conclusion

This design provides a comprehensive testing strategy that balances speed (unit tests) with confidence (integration tests). The hybrid approach ensures critical business logic is thoroughly tested while maintaining fast feedback loops for development.

**Next steps:**
1. Create detailed implementation plan with writing-plans skill
2. Implement Phase 1 (Use Cases unit tests)
3. Implement Phase 2 (App layer tests)
4. Implement Phase 3 (Integration tests)
5. Review coverage reports and adjust as needed
