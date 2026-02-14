# Quick GitHub Setup (5 минут)

## ⚡ Минимальная критическая настройка

### 1️⃣ Branch Protection (ОБЯЗАТЕЛЬНО)

Откройте: https://github.com/kirich1409/agents-remote/settings/branches

#### Для `main`:

**Кликните "Add rule":**
- Branch name pattern: `main`
- ✅ Require a pull request before merging
- ✅ Require approvals: 1
- ✅ Require status checks to pass before merging
  - В поиске введите и выберите:
    - `Kotlin Quality Analysis`
    - `Unit & Integration Tests`
    - `Security Analysis`
- ✅ Require conversation resolution before merging
- ✅ Do not allow bypassing the above settings

**Сохраните** → "Create"

#### Для `develop`:

**Кликните "Add rule" (ещё раз):**
- Branch name pattern: `develop`
- ✅ Require a pull request before merging
- Approvals: 0 (можно оставить пустым)
- ✅ Require status checks to pass before merging
  - Выберите:
    - `Kotlin Quality Analysis`
    - `Unit & Integration Tests`
- ✅ Require conversation resolution before merging

**Сохраните** → "Create"

---

### 2️⃣ Security Features (ОБЯЗАТЕЛЬНО)

Откройте: https://github.com/kirich1409/agents-remote/settings/security_analysis

**Включите всё:**
- ✅ Dependency graph
- ✅ Dependabot alerts
- ✅ Dependabot security updates
- ✅ Secret scanning
- ✅ Push protection (блокирует push секретов)

---

### 3️⃣ GitHub Copilot (если есть подписка)

#### Если у вас GitHub Copilot Enterprise:

Откройте: https://github.com/kirich1409/agents-remote/settings/code_security_and_analysis

- ✅ GitHub Copilot Code Review

Настройте:
- Trigger: On every pull request
- Languages: Kotlin, Java, YAML, Dockerfile

#### Если у вас GitHub Copilot Pro/Individual:

Создайте файл `.github/workflows/copilot-review.yml`:

```yaml
name: Copilot Review

on:
  pull_request:
    types: [opened, synchronize]

permissions:
  contents: read
  pull-requests: write

jobs:
  review:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v4

      - name: Copilot Review
        uses: github/copilot-code-review-action@v1
        with:
          github-token: ${{ secrets.GITHUB_TOKEN }}
```

Закоммитьте в `main`.

---

### 4️⃣ Dependabot (автообновления)

Создайте файл `.github/dependabot.yml`:

```yaml
version: 2
updates:
  - package-ecosystem: "gradle"
    directory: "/"
    schedule:
      interval: "weekly"
    open-pull-requests-limit: 5

  - package-ecosystem: "github-actions"
    directory: "/"
    schedule:
      interval: "weekly"
    open-pull-requests-limit: 3

  - package-ecosystem: "docker"
    directory: "/"
    schedule:
      interval: "weekly"
    open-pull-requests-limit: 3
```

Закоммитьте в `main`.

---

## ✅ Проверка

После настройки выполните:

```bash
# 1. Проверьте protection rules
gh api repos/kirich1409/agents-remote/branches/main/protection --jq '.required_status_checks.contexts'

# Должно вывести:
# [
#   "Kotlin Quality Analysis",
#   "Unit & Integration Tests",
#   "Security Analysis"
# ]

# 2. Попробуйте создать тестовый PR
git checkout develop
git checkout -b test/protection-check
echo "test" >> README.md
git add README.md
git commit -m "test: verify protection"
git push -u origin test/protection-check
gh pr create --base develop --title "Test: Protection Check"

# 3. Проверьте, что CI запустился
gh pr checks

# 4. Попробуйте смержить без approval (должно быть заблокировано)
gh pr merge --squash
# Должно выдать ошибку: "Required approvals not met"

# 5. Удалите тестовый PR
gh pr close
git checkout develop
git branch -D test/protection-check
```

---

## 🎯 Результат

После setup у вас:

**Защита:**
- ❌ Нельзя push напрямую в main/develop
- ❌ Нельзя merge без прохождения CI
- ❌ Нельзя merge с незакрытыми conversations
- ❌ Нельзя push секреты (push protection)
- ✅ Автоматические security alerts

**Автоматизация:**
- ✅ CI на каждый PR (~7 мин)
- ✅ Copilot review (если настроен)
- ✅ Dependabot автообновления (еженедельно)
- ✅ Secret scanning (постоянно)

**Время на setup:** ~5 минут вручную

---

## 🚀 Следующие шаги

После базовой настройки можно добавить:

1. **CodeQL Analysis** (лучший static analyzer)
   - См. `docs/GITHUB_SETUP.md` → раздел 4

2. **Codecov integration** (test coverage badges)
   - https://about.codecov.io/

3. **Slack notifications**
   - GitHub app в Slack workspace

4. **Issue templates**
   - См. `docs/GITHUB_SETUP.md` → раздел 7

Полная документация: `docs/GITHUB_SETUP.md`
