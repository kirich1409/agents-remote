# Миграция на Android Gradle Plugin 9.0.1 — План реализации

> **For Claude:** REQUIRED SUB-SKILL: Use superpowers:executing-plans to implement this plan task-by-task.

**Goal:** Обновить проект до AGP 9.0.1 с отключением всех флагов совместимости (no opt-out), полная миграция на новый DSL и встроенную поддержку Kotlin. Встроенный Kotlin используется, но версия переопределяется на 2.3.10 (текущая версия проекта).

**Architecture:** Проект — Kotlin Multiplatform (shared + backend + app). Модуль `shared` использует `com.android.library` и должен быть мигрирован на `com.android.kotlin.multiplatform.library`. Модуль `app` использует `com.android.application` + KMP и должен быть разделён на два модуля: чистый `androidApp` (application) и `app` (KMP library). Backend модуль не затрагивается AGP, но нужен Gradle 9.3.1+.

**Tech Stack:** Gradle 9.3.1, AGP 9.0.1, Kotlin 2.3.10 (>= 2.2.10 required), KSP, Compose Multiplatform 1.10.1, SQLDelight 2.2.1

**Источники:**
- [AGP 9.0.1 Release Notes](https://developer.android.com/build/releases/agp-9-0-0-release-notes)
- [JetBrains: Update KMP for AGP 9](https://blog.jetbrains.com/kotlin/2026/01/update-your-projects-for-agp9/)
- [Kotlin docs: KMP AGP 9 Migration](https://kotlinlang.org/docs/multiplatform/multiplatform-project-agp-9-migration.html)
- [nek12.dev: AGP 9 Migration Guide](https://nek12.dev/blog/en/agp-9-0-migration-guide-android-gradle-plugin-9-kmp-migration-kotlin)

---

## Текущее состояние проекта

| Компонент | Текущая версия | Целевая версия |
|-----------|---------------|----------------|
| Gradle | 8.14.4 | 9.3.1 |
| AGP | 8.7.3 | 9.0.1 |
| Kotlin | 2.3.10 | 2.3.10 (совместим) |
| KSP | 2.3.5 | 2.3.5 (проверить совместимость) |
| compileSdk | 35 | 36 |
| targetSdk | 35 (shared), 34 (app) | 36 |
| Build Tools | по умолчанию | 36.0.0 |

### Модули проекта:
- **`:shared`** — KMP library (`com.android.library` + `kotlin("multiplatform")`) → нужна миграция на `com.android.kotlin.multiplatform.library`
- **`:app`** — KMP app (`com.android.application` + `kotlin("multiplatform")`) → нужно разделить на `:androidApp` + `:app` (library)
- **`:backend`** — JVM only (`kotlin("jvm")`) → изменения только из-за Gradle 9.3.1

### Флаги совместимости, которые мы НЕ включаем (полная миграция):
- `android.newDsl` — используем новый DSL (default `true`)
- `android.builtInKotlin` — встроенная поддержка Kotlin (default `true`), версия переопределяется на 2.3.10 через KGP в classpath
- `android.enableLegacyVariantApi` — не используем legacy variant API
- `android.defaults.buildfeatures.resvalues` — `false` по умолчанию
- `android.defaults.buildfeatures.shaders` — `false` по умолчанию
- `android.defaults.buildfeatures.aidl` — `false` по умолчанию
- `android.defaults.buildfeatures.renderscript` — `false` по умолчанию
- `android.enableAppCompileTimeRClass` — `true` по умолчанию
- `android.uniquePackageNames` — `true` по умолчанию
- `android.r8.strictFullModeForKeepRules` — `true` по умолчанию
- Все остальные — принимаем новые дефолты AGP 9.0

---

## Task 1: Обновить Gradle wrapper до 9.1+

**Files:**
- Modify: `gradle/wrapper/gradle-wrapper.properties`

**Step 1: Обновить версию Gradle**

Run: `cd /Users/krozov/dev/projects/agents-remote && ./gradlew wrapper --gradle-version=9.3.1`

**Step 2: Проверить что wrapper обновился**

Run: `./gradlew --version`
Expected: Gradle 9.3.1

**Step 3: Проверить сборку с текущим AGP (совместимость Gradle 9.3.1 + AGP 8.7.3)**

Run: `./gradlew help`
Expected: Возможен FAIL — AGP 8.7.3 может не поддерживать Gradle 9.3.1. Это ожидаемо, переходим к Task 2.

**Step 4: Commit**

```bash
git add gradle/wrapper/
git commit -m "chore: upgrade Gradle wrapper to 9.3.1"
```

---

## Task 2: Обновить версии AGP и SDK в version catalog

**Files:**
- Modify: `gradle/libs.versions.toml`

**Step 1: Обновить версии**

В `gradle/libs.versions.toml`:
```toml
[versions]
gradle = "9.3.1"          # было 8.14.4
agp = "9.0.1"             # было 8.7.3
android-compileSdk = "36"  # было 35
android-targetSdk = "36"   # было 35

[plugins]
# Добавить новый плагин для KMP Android library
android-kmp-library = { id = "com.android.kotlin.multiplatform.library", version.ref = "agp" }
```

**Step 2: Убрать `composeCompiler` версию из toml** (если есть, AGP 9 управляет Kotlin/Compose автоматически)

Удалить строку: `composeCompiler = "1.5.11"` — Compose Compiler теперь встроен в KGP 2.x.

**Step 3: Commit**

```bash
git add gradle/libs.versions.toml
git commit -m "chore: update AGP to 9.0.1, compileSdk/targetSdk to 36"
```

---

## Task 3: Обновить gradle.properties — убрать устаревшие и добавить нужные свойства

**Files:**
- Modify: `gradle.properties`

**Step 1: Обновить gradle.properties**

Все флаги AGP 9.0 указываем явно — осознанный выбор новых дефолтов, без opt-out:

```properties
# Kotlin
kotlin.code.style=official

# Gradle
org.gradle.jvmargs=-Xmx4g -XX:MaxMetaspaceSize=1g
org.gradle.parallel=true
org.gradle.caching=true

# Android — AGP 9.0 flags (explicit, no opt-out)
android.useAndroidX=true

# New DSL and built-in Kotlin
android.newDsl=true
android.builtInKotlin=true
android.enableLegacyVariantApi=false

# Build features (disabled by default in AGP 9.0)
android.defaults.buildfeatures.resvalues=false
android.defaults.buildfeatures.shaders=false
android.defaults.buildfeatures.aidl=false
android.defaults.buildfeatures.renderscript=false

# SDK defaults
android.default.androidx.test.runner=true
android.sdk.defaultTargetSdkToCompileSdkIfUnset=true

# Code generation and optimization
android.enableAppCompileTimeRClass=true
android.uniquePackageNames=true
android.onlyEnableUnitTestForTheTestedBuildType=true
android.dependency.useConstraints=false

# ProGuard / R8
android.proguard.failOnMissingFiles=true
android.r8.optimizedResourceShrinking=true
android.r8.strictFullModeForKeepRules=true
android.r8.proguardAndroidTxt.disallowed=true
android.r8.globalOptionsInConsumerRules.disallowed=true

# Source sets
android.sourceset.disallowProvider=true
```

**Step 2: Commit**

```bash
git add gradle.properties
git commit -m "chore: clean gradle.properties for AGP 9.0 migration"
```

---

## Task 4: Мигрировать модуль `:shared` на `com.android.kotlin.multiplatform.library`

**Files:**
- Modify: `shared/build.gradle.kts`

**Step 1: Заменить плагины**

Было:
```kotlin
plugins {
    kotlin("multiplatform")
    id("com.android.library")
    kotlin("plugin.serialization")
    id("com.google.devtools.ksp")
    id("app.cash.sqldelight")
    id("io.gitlab.arturbosch.detekt")
    id("org.jmailen.kotlinter")
}
```

Стало:
```kotlin
plugins {
    id("com.android.kotlin.multiplatform.library")
    kotlin("plugin.serialization")
    id("com.google.devtools.ksp")
    id("app.cash.sqldelight")
    id("io.gitlab.arturbosch.detekt")
    id("org.jmailen.kotlinter")
}
```

Примечание: `kotlin("multiplatform")` больше не нужен — `com.android.kotlin.multiplatform.library` включает KMP поддержку. `com.android.library` заменяется новым плагином.

**Step 2: Заменить блок `android {}` на `kotlin.androidLibrary {}`**

Было:
```kotlin
android {
    namespace = "com.example.rcc.shared"
    compileSdk = 35

    defaultConfig {
        minSdk = 24
    }
}
```

Стало (внутри блока `kotlin {}`):
```kotlin
kotlin {
    androidLibrary {
        namespace = "com.example.rcc.shared"
        compileSdk = 36
        minSdk = 24
    }
    // ...
}
```

**Step 3: Убрать `androidTarget()` из `kotlin {}` блока**

`androidLibrary {}` уже определяет android target. Вызов `androidTarget()` больше не нужен.

**Step 4: Заменить `androidMain` на новый формат source set**

Было:
```kotlin
val androidMain by getting {
    dependencies {
        implementation(libs.sqldelight.android.driver)
        implementation(libs.ktor.client.okhttp)
    }
}
```

Стало (вынести в корневой `dependencies {}` блок, если новый плагин не поддерживает source set dependencies):
```kotlin
// В корне файла, после kotlin {} блока
dependencies {
    // Android-specific dependencies
    add("androidMainImplementation", libs.sqldelight.android.driver)
    add("androidMainImplementation", libs.ktor.client.okhttp)
}
```

Или, если плагин поддерживает source set dependencies в kotlin {} блоке — оставить как есть, но изменить имя source set. Нужно проверить документацию плагина для точного синтаксиса.

**Step 5: Заменить `androidUnitTest` → `androidHostTest`** (если используется)

**Step 6: Включить androidResources если нужно**

Если shared модуль использует Android resources:
```kotlin
androidLibrary {
    namespace = "com.example.rcc.shared"
    compileSdk = 36
    minSdk = 24
    androidResources {
        enable = true
    }
}
```

**Step 7: Проверить сборку**

Run: `./gradlew :shared:assemble`
Expected: BUILD SUCCESSFUL

**Step 8: Commit**

```bash
git add shared/build.gradle.kts
git commit -m "feat: migrate :shared to com.android.kotlin.multiplatform.library"
```

---

## Task 5: Разделить модуль `:app` — создать `:androidApp`

Модуль `:app` использует `com.android.application` + `kotlin("multiplatform")` — эта комбинация запрещена в AGP 9. Нужно:
1. Создать отдельный `:androidApp` модуль с `com.android.application` (без KMP)
2. Превратить `:app` в KMP library

**Files:**
- Create: `androidApp/build.gradle.kts`
- Create: `androidApp/src/main/AndroidManifest.xml` (если нужен)
- Move: `app/src/androidMain/` → `androidApp/src/main/` (Activity, Application)
- Modify: `app/build.gradle.kts`
- Modify: `settings.gradle.kts`

**Step 1: Создать `androidApp/build.gradle.kts`**

```kotlin
plugins {
    id("com.android.application")
    kotlin("plugin.compose")
    id("org.jetbrains.compose")
}

android {
    namespace = "com.example.rcc"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.example.rcc"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0.0"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }
}

dependencies {
    implementation(project(":app"))
    implementation(libs.androidx.activity.compose)
}
```

Примечание: `kotlin("android")` не нужен — встроен в AGP 9.0 (`android.builtInKotlin=true`). Kotlin версия 2.3.10 подхватывается автоматически из KGP в classpath (объявлен в корневом `build.gradle.kts`).

**Step 2: Перенести Android-specific код**

Переместить `MainActivity` (и прочие Android entry point файлы) из `app/src/androidMain/` в `androidApp/src/main/kotlin/`.

**Step 3: Перенести `AndroidManifest.xml`**

Переместить из `app/src/androidMain/AndroidManifest.xml` в `androidApp/src/main/AndroidManifest.xml`.

**Step 4: Добавить `:androidApp` в `settings.gradle.kts`**

```kotlin
include(":androidApp")
```

**Step 5: Проверить сборку**

Run: `./gradlew :androidApp:assembleDebug`
Expected: BUILD SUCCESSFUL

**Step 6: Commit**

```bash
git add androidApp/ settings.gradle.kts
git commit -m "feat: create :androidApp module for Android entry point"
```

---

## Task 6: Мигрировать модуль `:app` в KMP library

**Files:**
- Modify: `app/build.gradle.kts`

**Step 1: Заменить плагины**

Было:
```kotlin
plugins {
    kotlin("multiplatform")
    kotlin("plugin.serialization")
    kotlin("plugin.compose")
    id("org.jetbrains.compose")
    id("com.android.application")
    id("com.google.devtools.ksp")
    id("io.gitlab.arturbosch.detekt")
    id("org.jmailen.kotlinter")
}
```

Стало:
```kotlin
plugins {
    id("com.android.kotlin.multiplatform.library")
    kotlin("plugin.serialization")
    kotlin("plugin.compose")
    id("org.jetbrains.compose")
    id("com.google.devtools.ksp")
    id("io.gitlab.arturbosch.detekt")
    id("org.jmailen.kotlinter")
}
```

**Step 2: Заменить `android {}` на `kotlin.androidLibrary {}`**

Убрать весь `android {}` блок. Добавить в `kotlin {}`:
```kotlin
kotlin {
    androidLibrary {
        namespace = "com.example.rcc.app"
        compileSdk = 36
        minSdk = 24
        androidResources {
            enable = true
        }
    }
    // ...
}
```

**Step 3: Убрать `androidTarget()` из `kotlin {}`**

**Step 4: Убрать Android-specific dependencies** из `androidMain` source set

Перенести `libs.sqldelight.android.driver`, `libs.ktor.client.okhttp` в корневой `dependencies {}` или оставить в source set (зависит от поддержки плагином).

Убрать `libs.androidx.activity.compose` — он теперь в `:androidApp`.

**Step 5: Убрать `compose.desktop` блок** — он должен остаться если desktop target поддерживается

**Step 6: Проверить сборку**

Run: `./gradlew :app:assemble`
Expected: BUILD SUCCESSFUL

**Step 7: Commit**

```bash
git add app/build.gradle.kts
git commit -m "feat: migrate :app to KMP library for AGP 9.0"
```

---

## Task 7: Обновить корневой `build.gradle.kts`

**Files:**
- Modify: `build.gradle.kts`

**Step 1: Обновить объявления плагинов**

Было:
```kotlin
plugins {
    kotlin("multiplatform") version "2.3.10" apply false
    kotlin("jvm") version "2.3.10" apply false
    kotlin("plugin.serialization") version "2.3.10" apply false
    kotlin("plugin.compose") version "2.3.10" apply false
    id("org.jetbrains.compose") version "1.10.1" apply false
    id("com.android.application") version "8.7.3" apply false
    id("com.android.library") version "8.7.3" apply false
    id("com.google.devtools.ksp") version "2.3.5" apply false
    id("app.cash.sqldelight") version "2.2.1" apply false
    id("io.gitlab.arturbosch.detekt") version "1.23.6" apply false
    id("org.jmailen.kotlinter") version "4.4.1" apply false
}
```

Стало:
```kotlin
plugins {
    kotlin("multiplatform") version "2.3.10" apply false
    kotlin("jvm") version "2.3.10" apply false
    kotlin("plugin.serialization") version "2.3.10" apply false
    kotlin("plugin.compose") version "2.3.10" apply false
    id("org.jetbrains.compose") version "1.10.1" apply false
    id("com.android.application") version "9.0.1" apply false
    id("com.android.library") version "9.0.1" apply false
    id("com.android.kotlin.multiplatform.library") version "9.0.1" apply false
    id("com.google.devtools.ksp") version "2.3.5" apply false
    id("app.cash.sqldelight") version "2.2.1" apply false
    id("io.gitlab.arturbosch.detekt") version "1.23.6" apply false
    id("org.jmailen.kotlinter") version "4.4.1" apply false
}
```

Добавлен `com.android.kotlin.multiplatform.library`. AGP версия обновлена на `9.0.1`.

Примечание: Kotlin плагины (`kotlin("multiplatform")`, `kotlin("jvm")` и т.д.) остаются с `version "2.3.10"`. AGP 9.0 использует встроенный Kotlin (default KGP 2.2.10), но при наличии KGP более высокой версии в classpath — Gradle автоматически поднимет до 2.3.10. Плагин `kotlin("android")` **убираем** — он не нужен, встроенный Kotlin его заменяет.

**Step 2: Проверить совместимость `KotlinCompile` task configuration**

В блоке `allprojects` используется `tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>()` — это может работать иначе с встроенным Kotlin в AGP 9. Проверить что конфигурация `jvmTarget` и `freeCompilerArgs` всё ещё применяется.

**Step 3: Проверить сборку**

Run: `./gradlew help`
Expected: BUILD SUCCESSFUL

**Step 4: Commit**

```bash
git add build.gradle.kts
git commit -m "chore: update root build.gradle.kts for AGP 9.0.1"
```

---

## Task 8: Обновить `settings.gradle.kts` — раскомментировать `:app` и добавить `:androidApp`

**Files:**
- Modify: `settings.gradle.kts`

**Step 1: Обновить includes**

```kotlin
rootProject.name = "remote-cloud-code"

include(":shared")
include(":backend")
include(":app")
include(":androidApp")
```

**Step 2: Commit**

```bash
git add settings.gradle.kts
git commit -m "chore: enable :app and add :androidApp modules"
```

---

## Task 9: Полная проверка сборки всех модулей

**Step 1: Sync проект**

Run: `./gradlew --refresh-dependencies help`

**Step 2: Собрать shared**

Run: `./gradlew :shared:assemble`
Expected: BUILD SUCCESSFUL

**Step 3: Собрать backend**

Run: `./gradlew :backend:assemble`
Expected: BUILD SUCCESSFUL

**Step 4: Собрать app**

Run: `./gradlew :app:assemble`
Expected: BUILD SUCCESSFUL

**Step 5: Собрать androidApp**

Run: `./gradlew :androidApp:assembleDebug`
Expected: BUILD SUCCESSFUL

**Step 6: Запустить тесты**

Run: `./gradlew test`
Expected: BUILD SUCCESSFUL, all tests pass

**Step 7: Commit финальный**

```bash
git add -A
git commit -m "feat: complete migration to AGP 9.0.1 with all compatibility flags disabled"
```

---

## Task 10: Проверить что флаги совместимости НЕ используются

**Step 1: Проверить gradle.properties**

Убедиться что:
- Все флаги AGP 9.0 указаны явно с новыми дефолтами (см. Task 3)
- НЕТ opt-out флагов: `android.newDsl=false`, `android.builtInKotlin=false`, `android.enableLegacyVariantApi=true`
- НЕТ запрещённых флагов (вызывают ошибку сборки в AGP 9):
  - `android.r8.integratedResourceShrinking`
  - `android.enableNewResourceShrinker.preciseShrinking`

**Step 2: Проверить что нет deprecated API usage**

Run: `./gradlew assembleDebug --warning-mode all 2>&1 | grep -i "deprecated\|legacy\|removed"`
Expected: Нет предупреждений связанных с AGP deprecated APIs

**Step 3: Финальный commit если были изменения**

---

## Известные риски и fallback

1. **SQLDelight 2.2.1** — может не поддерживать `com.android.kotlin.multiplatform.library`. Если нет — оставить `android.enableLegacyVariantApi=true` временно для `:shared` модуля или обновить SQLDelight.

2. **KSP** — проверить совместимость с AGP 9.0. Если KSP не работает — обновить до совместимой версии.

3. **Detekt/Kotlinter** — могут требовать обновления для Gradle 9.3.1.

4. **Compose Multiplatform 1.10.1** — проверить совместимость с новым KMP Android library плагином.

5. **Модуль `:app` временно отключён** — в `settings.gradle.kts` он закомментирован. Миграция Tasks 5-6 актуальна только после его включения. Можно пропустить Tasks 5-6 если модуль не нужен сейчас.
