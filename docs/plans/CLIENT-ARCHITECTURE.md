# Client Architecture: Decompose + MVIKotlin

**Версия:** MVP
**Паттерн:** MVI (Model-View-Intent)
**Фреймворки:** Decompose + MVIKotlin + Essenty
**Целевые платформы:** Android, Desktop JVM, iOS

---

## 1. Architecture Overview

### Слои архитектуры

```
┌─────────────────────────────────────┐
│   UI Layer (Compose Multiplatform)  │
│   - Screens, Components             │
│   - Material 3 Design               │
│   - Responsive layouts              │
└────────────┬────────────────────────┘
             │
┌────────────▼────────────────────────┐
│   Presentation Layer                │
│   - Components (Decompose)          │
│   - Stores (MVIKotlin)              │
│   - State (MVI pattern)             │
└────────────┬────────────────────────┘
             │
┌────────────▼────────────────────────┐
│   Domain Layer                      │
│   - Use Cases                       │
│   - Business Logic                  │
│   - Repository Interfaces           │
└────────────┬────────────────────────┘
             │
┌────────────▼────────────────────────┐
│   Data Layer                        │
│   - Repository Implementations      │
│   - API Client (Ktor)               │
│   - Local Cache (SQLDelight)        │
│   - Models, DTOs                    │
└─────────────────────────────────────┘
```

---

## 2. Decompose Component Tree

### Структура навигации

```
RootComponent (root routing)
├── BottomSheetComponent (authentication flow)
│   └── AuthScreen
│
├── ChatListComponent (main navigation)
│   ├── ChatListStore (MVIKotlin)
│   ├── ChatListScreen
│   └── ChildStack
│       ├── ChatDetailComponent
│       │   ├── ChatDetailStore (MVIKotlin)
│       │   └── ChatDetailScreen
│       │
│       └── SettingsComponent
│           ├── SettingsStore (MVIKotlin)
│           └── SettingsScreen
│
└── BackStack Management (Decompose lifecycle)
```

### Example: RootComponent

```kotlin
// Root interface (for DI)
interface RootComponent {
    val childStack: Value<ChildStack<*, Child>>

    sealed class Child {
        data class ChatListChild(val component: ChatListComponent) : Child()
        data class SettingsChild(val component: SettingsComponent) : Child()
    }
}

// Root implementation (with Decompose routing)
class RootComponentImpl(
    componentContext: ComponentContext,
    private val chatListComponentFactory: (ComponentContext) -> ChatListComponent,
    private val settingsComponentFactory: (ComponentContext) -> SettingsComponent
) : RootComponent, ComponentContext by componentContext {

    override val childStack: Value<ChildStack<*, RootComponent.Child>> =
        childStack(
            source = navigation,
            serializer = null,
            initialStack = { listOf(Config.ChatList) },
            childFactory = { config, context ->
                when (config) {
                    Config.ChatList -> RootComponent.Child.ChatListChild(
                        chatListComponentFactory(context)
                    )
                    Config.Settings -> RootComponent.Child.SettingsChild(
                        settingsComponentFactory(context)
                    )
                }
            }
        )

    private val navigation = StackNavigation<Config>()

    sealed class Config {
        object ChatList : Config()
        object Settings : Config()
    }
}
```

---

## 3. MVIKotlin Store Pattern

### MVI Concepts

```
Intent (User action)
   ↓
Store (receives Intent)
   ├── Middleware (side effects: API calls, DB)
   └── Reducer (updates State)
   ↓
State (new state)
   ↓
View (observes State, renders UI)
```

### Example: ChatListStore

```kotlin
// Intent (actions)
sealed class ChatListIntent {
    object LoadChats : ChatListIntent()
    data class SelectChat(val id: String) : ChatListIntent()
    object CreateNewChat : ChatListIntent()
    object DeleteChat(val id: String) : ChatListIntent()
}

// State (model)
data class ChatListState(
    val chats: List<Chat> = emptyList(),
    val selectedChatId: String? = null,
    val isLoading: Boolean = false,
    val error: String? = null
) {
    val selectedChat: Chat? get() = chats.find { it.id == selectedChatId }
}

// MVIKotlin Store
class ChatListStoreFactory(
    private val repository: ChatRepository,
    private val storeFactory: StoreFactory
) {

    fun create(componentContext: ComponentContext): ChatListStore =
        object : ChatListStore,
            Store<ChatListIntent, ChatListState, Nothing> by storeFactory.create(
                name = "ChatListStore",
                initialState = ChatListState(),
                executorFactory = ::ChatListExecutor,
                reducer = ::chatListReducer
            ) {}

    // Side effects (API calls, DB, etc.)
    inner class ChatListExecutor : SimpleExecutor<ChatListIntent, ChatListState, Nothing>() {
        override fun executeIntent(intent: ChatListIntent) {
            when (intent) {
                ChatListIntent.LoadChats -> loadChats()
                is ChatListIntent.SelectChat -> selectChat(intent.id)
                ChatListIntent.CreateNewChat -> createNewChat()
                is ChatListIntent.DeleteChat -> deleteChat(intent.id)
            }
        }

        private fun loadChats() {
            scope.launch {
                try {
                    val chats = repository.getChats()
                    dispatch(ChatListAction.ChatsLoaded(chats))
                } catch (e: Exception) {
                    dispatch(ChatListAction.LoadingFailed(e.message ?: "Unknown error"))
                }
            }
        }

        private fun selectChat(id: String) {
            dispatch(ChatListAction.ChatSelected(id))
        }

        private fun createNewChat() {
            scope.launch {
                try {
                    val chat = repository.createChat()
                    dispatch(ChatListAction.ChatCreated(chat))
                } catch (e: Exception) {
                    dispatch(ChatListAction.CreationFailed(e.message ?: "Unknown error"))
                }
            }
        }

        private fun deleteChat(id: String) {
            scope.launch {
                try {
                    repository.deleteChat(id)
                    dispatch(ChatListAction.ChatDeleted(id))
                } catch (e: Exception) {
                    dispatch(ChatListAction.DeletionFailed(e.message ?: "Unknown error"))
                }
            }
        }
    }

    // Actions (internal state changes)
    sealed class ChatListAction {
        data class ChatsLoaded(val chats: List<Chat>) : ChatListAction()
        data class ChatSelected(val id: String) : ChatListAction()
        data class ChatCreated(val chat: Chat) : ChatListAction()
        data class ChatDeleted(val id: String) : ChatListAction()
        data class LoadingFailed(val error: String) : ChatListAction()
        data class CreationFailed(val error: String) : ChatListAction()
        data class DeletionFailed(val error: String) : ChatListAction()
    }
}

// Reducer (state updates)
fun chatListReducer(state: ChatListState, action: ChatListAction): ChatListState =
    when (action) {
        is ChatListAction.ChatsLoaded -> state.copy(
            chats = action.chats,
            isLoading = false,
            error = null
        )
        is ChatListAction.ChatSelected -> state.copy(
            selectedChatId = action.id
        )
        is ChatListAction.ChatCreated -> state.copy(
            chats = state.chats + action.chat,
            isLoading = false
        )
        is ChatListAction.ChatDeleted -> state.copy(
            chats = state.chats.filter { it.id != action.id }
        )
        is ChatListAction.LoadingFailed -> state.copy(
            isLoading = false,
            error = action.error
        )
        is ChatListAction.CreationFailed -> state.copy(
            error = action.error
        )
        is ChatListAction.DeletionFailed -> state.copy(
            error = action.error
        )
    }

// Store interface (for injection)
interface ChatListStore : Store<ChatListIntent, ChatListState, Nothing>
```

---

## 4. Component Implementation

### ChatListComponent (with MVIKotlin Store)

```kotlin
// Component interface
interface ChatListComponent {
    val store: ChatListStore
    fun onChatClick(chatId: String)
    fun onNewChatClick()
    fun onDeleteClick(chatId: String)
}

// Component implementation
class ChatListComponentImpl(
    componentContext: ComponentContext,
    private val repository: ChatRepository,
    private val storeFactory: StoreFactory,
    private val onNavigateToDetail: (String) -> Unit
) : ChatListComponent, ComponentContext by componentContext {

    override val store: ChatListStore = ChatListStoreFactory(repository, storeFactory).create(
        componentContext
    )

    init {
        // Load chats on component creation
        store.accept(ChatListIntent.LoadChats)
    }

    override fun onChatClick(chatId: String) {
        store.accept(ChatListIntent.SelectChat(chatId))
        onNavigateToDetail(chatId)
    }

    override fun onNewChatClick() {
        store.accept(ChatListIntent.CreateNewChat)
    }

    override fun onDeleteClick(chatId: String) {
        store.accept(ChatListIntent.DeleteChat(chatId))
    }
}
```

---

## 5. UI Layer (Compose)

### ChatListScreen (observing MVIKotlin Store)

```kotlin
@Composable
fun ChatListScreen(
    component: ChatListComponent,
    modifier: Modifier = Modifier
) {
    val state by component.store.stateFlow.collectAsState()

    Surface(modifier = modifier.fillMaxSize()) {
        when {
            state.isLoading && state.chats.isEmpty() -> {
                LoadingIndicator()
            }
            state.error != null -> {
                ErrorScreen(
                    error = state.error,
                    onRetry = { component.store.accept(ChatListIntent.LoadChats) }
                )
            }
            state.chats.isEmpty() -> {
                EmptyScreen(
                    onNewChat = component::onNewChatClick
                )
            }
            else -> {
                ChatListContent(
                    chats = state.chats,
                    selectedChatId = state.selectedChatId,
                    onChatClick = component::onChatClick,
                    onDeleteClick = component::onDeleteClick,
                    onNewChatClick = component::onNewChatClick
                )
            }
        }
    }
}

@Composable
fun ChatListContent(
    chats: List<Chat>,
    selectedChatId: String?,
    onChatClick: (String) -> Unit,
    onDeleteClick: (String) -> Unit,
    onNewChatClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val windowSizeClass = calculateWindowSizeClass()

    when (windowSizeClass.widthSizeClass) {
        WindowWidthSizeClass.Compact -> {
            // Phone: single pane
            ChatListSinglePane(
                chats = chats,
                onChatClick = onChatClick,
                onDeleteClick = onDeleteClick,
                onNewChatClick = onNewChatClick,
                modifier = modifier
            )
        }
        WindowWidthSizeClass.Expanded -> {
            // Tablet/Desktop: dual pane
            Row(modifier = modifier) {
                ChatListPane(
                    chats = chats,
                    selectedChatId = selectedChatId,
                    onChatClick = onChatClick,
                    onDeleteClick = onDeleteClick,
                    modifier = Modifier
                        .width(400.dp)
                        .fillMaxHeight()
                )
                ChatDetailPlaceholder(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                )
            }
        }
        else -> {
            ChatListSinglePane(
                chats = chats,
                onChatClick = onChatClick,
                onDeleteClick = onDeleteClick,
                onNewChatClick = onNewChatClick,
                modifier = modifier
            )
        }
    }
}
```

---

## 6. Dependency Injection (Koin + Decompose)

```kotlin
// Koin modules
val presentationModule = module {
    factory<StoreFactory> {
        LoggingStoreFactory(
            delegate = SimpleStoreFactory(),
            logger = PrintLogger()
        )
    }

    // Component factories
    factory { (componentContext: ComponentContext) ->
        ChatListComponentImpl(
            componentContext = componentContext,
            repository = get(),
            storeFactory = get(),
            onNavigateToDetail = {}
        ) as ChatListComponent
    }
}

val dataModule = module {
    single<ChatRepository> { ChatRepositoryImpl(get(), get()) }
    single { ApiClient(get()) }
    single { LocalCacheManager(get()) }
}

// Application setup
val appComponent = RootComponentImpl(
    componentContext = DefaultComponentContext(MainCoroutineDispatcher()),
    chatListComponentFactory = { context ->
        get<ChatListComponent> { parametersOf(context) }
    },
    settingsComponentFactory = { context ->
        get<SettingsComponent> { parametersOf(context) }
    }
)

startKoin {
    modules(presentationModule, dataModule)
}
```

---

## 7. Project Structure

```
app/src/commonMain/kotlin/
├── root/
│   ├── RootComponent.kt
│   └── RootComponentImpl.kt
│
├── features/
│   ├── chatlist/
│   │   ├── component/
│   │   │   ├── ChatListComponent.kt
│   │   │   └── ChatListComponentImpl.kt
│   │   ├── store/
│   │   │   ├── ChatListStore.kt
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
├── domain/
│   ├── usecase/
│   │   ├── GetChatsUseCase.kt
│   │   ├── SendMessageUseCase.kt
│   │   └── CreateChatUseCase.kt
│   ├── model/
│   │   ├── Chat.kt
│   │   └── Message.kt
│   └── repository/
│       └── ChatRepository.kt
│
├── data/
│   ├── repository/
│   │   └── ChatRepositoryImpl.kt
│   ├── api/
│   │   └── ApiClient.kt
│   ├── cache/
│   │   └── LocalCacheManager.kt
│   └── model/
│       ├── ChatDto.kt
│       └── MessageDto.kt
│
├── theme/
│   ├── Theme.kt
│   ├── Color.kt
│   └── Typography.kt
│
├── di/
│   └── KoinSetup.kt
│
└── App.kt
```

---

## 8. Key Benefits

✅ **Unidirectional Data Flow**
- Intent → Store → State → UI
- Easy to understand and debug

✅ **Time Travel Debugging**
- MVIKotlin has logging and debugging tools
- Can replay actions

✅ **Testability**
- Store logic is pure (easy to test)
- Components can be tested in isolation

✅ **Scalability**
- Each feature is self-contained
- Easy to add new features

✅ **Multiplatform Ready**
- All code is in `commonMain`
- Works on Android, Desktop, iOS

✅ **Real-time Chat Friendly**
- Unidirectional flow perfect for reactive updates
- WebSocket events → Intents → Store → UI

---

## 9. Testing Strategy

### Unit Tests (Store Logic)

```kotlin
@Test
fun testLoadChatsSuccessfully() {
    val store = ChatListStoreFactory(mockRepository, storeFactory).create(context)
    store.accept(ChatListIntent.LoadChats)

    advanceUntilIdle()
    val state = store.state

    assertThat(state.chats).isNotEmpty()
    assertThat(state.isLoading).isFalse()
}

@Test
fun testSelectChatUpdatesState() {
    val store = createStore()
    store.accept(ChatListIntent.SelectChat("chat-1"))

    assertThat(store.state.selectedChatId).isEqualTo("chat-1")
}
```

### Integration Tests (Component + Store)

```kotlin
@Test
fun testComponentLoadsChatsOnCreation() {
    val component = ChatListComponentImpl(context, repository, storeFactory, {})

    advanceUntilIdle()
    val state = component.store.state

    assertThat(state.chats).isNotEmpty()
}
```

### UI Tests (Compose)

```kotlin
@Test
fun testChatListDisplaysChats() {
    composeTestRule.setContent {
        ChatListScreen(component)
    }

    composeTestRule
        .onNodeWithText("Chat 1")
        .assertIsDisplayed()
}
```

---

**End of Client Architecture Document**
