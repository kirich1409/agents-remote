# GitHub Repository Setup Guide

## 🎯 Цель
Настроить максимальную защиту и эффективность разработки с GitHub Copilot code review.

---

## 1️⃣ Branch Protection Rules

### Автоматическая настройка (рекомендуется)

```bash
# Запустите из корня репозитория
bash /tmp/github-setup-commands.sh
```

### Ручная настройка через UI

**Settings → Branches → Add rule**

#### Main Branch (`main`)
- **Branch name pattern:** `main`
- ✅ Require a pull request before merging
  - ✅ Require approvals: **1**
  - ✅ Dismiss stale PR approvals when new commits are pushed
  - ✅ Require review from Code Owners
- ✅ Require status checks to pass before merging
  - ✅ Require branches to be up to date before merging
  - **Required checks:**
    - `Kotlin Quality Analysis`
    - `Unit & Integration Tests`
    - `Security Analysis`
- ✅ Require conversation resolution before merging
- ✅ Do not allow bypassing the above settings
- ✅ Restrict who can push to matching branches (only via PR)

#### Develop Branch (`develop`)
- **Branch name pattern:** `develop`
- ✅ Require a pull request before merging
  - Require approvals: **0** (optional для develop)
- ✅ Require status checks to pass before merging
  - ✅ Require branches to be up to date before merging
  - **Required checks:**
    - `Kotlin Quality Analysis`
    - `Unit & Integration Tests`
- ✅ Require conversation resolution before merging
- ✅ Do not allow bypassing the above settings

---

## 2️⃣ GitHub Copilot Code Review Integration

### Включить Copilot Reviews

**Settings → Code security and analysis → Code review**

1. ✅ Enable **GitHub Copilot Code Review**
2. Настройки:
   - **Review trigger:** On every pull request
   - **Review focus:** Security, Performance, Best Practices
   - **Languages:** Kotlin, Java, YAML, Dockerfile

### Добавить Copilot в workflow

Создайте `.github/workflows/copilot-review.yml`:

```yaml
name: GitHub Copilot Code Review

on:
  pull_request:
    types: [opened, synchronize, reopened]

permissions:
  contents: read
  pull-requests: write
  issues: write

jobs:
  copilot-review:
    name: Copilot Code Review
    runs-on: ubuntu-latest
    timeout-minutes: 10

    steps:
      - name: Checkout code
        uses: actions/checkout@v4
        with:
          fetch-depth: 0

      - name: Run Copilot Code Review
        uses: github/copilot-code-review-action@v1
        with:
          github-token: ${{ secrets.GITHUB_TOKEN }}
          # Фокус на критичных областях
          review-focus: |
            - Security vulnerabilities
            - Performance issues
            - Kotlin best practices
            - Null safety
            - Coroutine usage
            - Memory leaks
            - SQL injection risks
          # Игнорировать некритичные файлы
          exclude: |
            - '**/*.md'
            - '**/*.txt'
            - '.github/**'
            - 'gradle/wrapper/**'

      - name: Post review summary
        if: always()
        uses: actions/github-script@v7
        with:
          script: |
            github.rest.issues.createComment({
              issue_number: context.issue.number,
              owner: context.repo.owner,
              repo: context.repo.repo,
              body: `🤖 **GitHub Copilot Code Review Complete**

              Check the "Files changed" tab for inline suggestions.`
            })
```

### Обновить CODEOWNERS для Copilot

Добавьте в `.github/CODEOWNERS`:

```
# Copilot reviews all PRs automatically
* @github/copilot
* @krozov
```

---

## 3️⃣ Repository Security Settings

**Settings → Code security and analysis**

### Обязательные настройки:

✅ **Dependency graph** — Enable
✅ **Dependabot alerts** — Enable
✅ **Dependabot security updates** — Enable
✅ **Dependabot version updates** — Enable
✅ **Code scanning** — Enable (GitHub CodeQL)
✅ **Secret scanning** — Enable
✅ **Push protection** — Enable (блокирует коммиты с секретами)

### Создать `.github/dependabot.yml`:

```yaml
version: 2
updates:
  # Gradle dependencies
  - package-ecosystem: "gradle"
    directory: "/"
    schedule:
      interval: "weekly"
      day: "monday"
    open-pull-requests-limit: 5
    reviewers:
      - "krozov"
    labels:
      - "dependencies"
      - "automated"
    commit-message:
      prefix: "chore(deps)"

  # GitHub Actions
  - package-ecosystem: "github-actions"
    directory: "/"
    schedule:
      interval: "weekly"
      day: "monday"
    open-pull-requests-limit: 3
    reviewers:
      - "krozov"
    labels:
      - "dependencies"
      - "github-actions"
    commit-message:
      prefix: "chore(ci)"

  # Docker
  - package-ecosystem: "docker"
    directory: "/"
    schedule:
      interval: "weekly"
      day: "monday"
    open-pull-requests-limit: 3
    reviewers:
      - "krozov"
    labels:
      - "dependencies"
      - "docker"
    commit-message:
      prefix: "chore(docker)"
```

---

## 4️⃣ GitHub Advanced Security (если доступно)

**Settings → Code security and analysis → GitHub Advanced Security**

✅ **Code scanning alerts** — CodeQL Analysis
✅ **Secret scanning alerts** — Push protection + Partner patterns
✅ **Supply chain security** — Dependency review on PRs

### Создать CodeQL workflow `.github/workflows/codeql.yml`:

```yaml
name: CodeQL Security Analysis

on:
  push:
    branches: [main, develop]
  pull_request:
    branches: [main, develop]
  schedule:
    - cron: '0 6 * * 1'  # Weekly Monday 6 AM

permissions:
  security-events: write
  contents: read
  pull-requests: write

jobs:
  analyze:
    name: CodeQL Analysis
    runs-on: ubuntu-latest
    timeout-minutes: 30

    strategy:
      fail-fast: false
      matrix:
        language: ['java', 'kotlin']

    steps:
      - name: Checkout code
        uses: actions/checkout@v4

      - name: Setup JDK 21
        uses: actions/setup-java@v4
        with:
          java-version: '21'
          distribution: 'temurin'

      - name: Initialize CodeQL
        uses: github/codeql-action/init@v3
        with:
          languages: ${{ matrix.language }}
          queries: security-extended,security-and-quality

      - name: Build project
        run: ./gradlew :shared:build :backend:build -x test --no-daemon

      - name: Perform CodeQL Analysis
        uses: github/codeql-action/analyze@v3
        with:
          category: "/language:${{ matrix.language }}"
```

---

## 5️⃣ Notifications & Integrations

### Email Notifications

**Settings → Notifications → Email preferences**

✅ Pull request reviews
✅ Pull request pushes
✅ CI activity (failures only)
✅ Dependabot alerts
✅ Security alerts

### Slack Integration (опционально)

1. **Slack App:** Install GitHub app
2. **Subscribe:** `/github subscribe kirich1409/agents-remote reviews comments ci`
3. **Channel:** #agents-remote-ci

---

## 6️⃣ Рекомендованные GitHub Apps

### Обязательные:

1. **Codecov** — test coverage reporting
   - Free for open source
   - Adds coverage comments to PRs

2. **LGTM / Semgrep** — дополнительный static analysis
   - Catches security issues detekt может пропустить

3. **Renovate** — автоматические dependency updates (альтернатива Dependabot)
   - Более гибкий, лучше группирует обновления

### Полезные:

4. **WIP** — блокирует merge PR с "[WIP]" в названии
5. **Mergify** — автоматический merge при прохождении всех checks
6. **Pull Panda** — аналитика по code review времени

---

## 7️⃣ Pull Request Templates (уже есть)

Уже созданы в Phase 1:
- ✅ `.github/pull_request_template.md`
- ✅ `.github/CODEOWNERS`

### Добавить Issue Templates

Создайте `.github/ISSUE_TEMPLATE/`:

**bug_report.yml:**
```yaml
name: 🐛 Bug Report
description: Report a bug or unexpected behavior
labels: ["bug", "needs-triage"]
body:
  - type: markdown
    attributes:
      value: |
        Thanks for reporting! Please fill out the details below.

  - type: textarea
    id: description
    attributes:
      label: Description
      description: Clear description of the bug
    validations:
      required: true

  - type: textarea
    id: reproduction
    attributes:
      label: Steps to Reproduce
      description: How to reproduce the issue
      placeholder: |
        1. Go to...
        2. Click on...
        3. See error
    validations:
      required: true

  - type: textarea
    id: expected
    attributes:
      label: Expected Behavior
      description: What should happen?
    validations:
      required: true

  - type: textarea
    id: actual
    attributes:
      label: Actual Behavior
      description: What actually happens?
    validations:
      required: true

  - type: textarea
    id: environment
    attributes:
      label: Environment
      description: |
        - OS: [e.g. macOS 14.0]
        - Kotlin version: [e.g. 2.1.0]
        - Gradle version: [e.g. 8.6]
    validations:
      required: false
```

**feature_request.yml:**
```yaml
name: ✨ Feature Request
description: Suggest a new feature or enhancement
labels: ["enhancement", "needs-triage"]
body:
  - type: textarea
    id: problem
    attributes:
      label: Problem Description
      description: What problem does this solve?
    validations:
      required: true

  - type: textarea
    id: solution
    attributes:
      label: Proposed Solution
      description: How would you solve it?
    validations:
      required: true

  - type: textarea
    id: alternatives
    attributes:
      label: Alternatives Considered
      description: Other approaches you've thought about
    validations:
      required: false
```

---

## 8️⃣ Проверка текущих настроек

```bash
# Проверить branch protection
gh api repos/kirich1409/agents-remote/branches/main/protection

# Проверить enabled security features
gh api repos/kirich1409/agents-remote | jq '.security_and_analysis'

# Список webhooks
gh api repos/kirich1409/agents-remote/hooks
```

---

## 9️⃣ Recommended Workflow with Copilot

### Идеальный процесс разработки:

1. **Создание feature branch**
   ```bash
   git checkout develop
   git checkout -b feature/phase-2-domain
   ```

2. **Разработка с local checks**
   - Pre-commit hooks автоматически проверяют код
   - ~3 минуты на каждый коммит

3. **Push и создание PR**
   ```bash
   git push -u origin feature/phase-2-domain
   gh pr create --base develop
   ```

4. **Автоматические проверки запускаются:**
   - ⏱️ GitHub Actions CI (~7 min):
     - Quality Checks (ktlint, detekt, build)
     - Tests (unit, integration)
     - Security (TruffleHog, secrets)
   - 🤖 **GitHub Copilot Review** (~2 min):
     - Security vulnerabilities
     - Performance issues
     - Best practices violations
     - Null safety issues
   - 🔍 CodeQL Analysis (~10 min, если настроен)

5. **Manual review**
   - Просмотр Copilot suggestions
   - Code owner (@krozov) review
   - Resolve conversations

6. **Auto-merge после approval** (если настроен Mergify)

---

## 🔟 Checklist для завершения setup

- [ ] Branch protection rules созданы (main + develop)
- [ ] GitHub Copilot Code Review включен
- [ ] Dependabot настроен (dependabot.yml)
- [ ] CodeQL workflow добавлен
- [ ] Secret scanning + push protection включены
- [ ] Issue templates созданы
- [ ] CODEOWNERS обновлён (добавлен @github/copilot)
- [ ] Notifications настроены
- [ ] Slack integration (опционально)
- [ ] Codecov integration (рекомендуется)

---

## 📊 Метрики защиты

После настройки у вас будет:

**6-layer protection:**
1. Pre-commit hooks (local, ~3 min)
2. GitHub Actions CI (remote, ~7 min)
3. **GitHub Copilot Review (автоматический, ~2 min)** ← новое
4. CodeQL Security Analysis (weekly + PR)
5. Branch Protection (PR required, checks enforced)
6. Code Owners Review (manual, @krozov)

**Security coverage:**
- ✅ Static analysis (detekt + CodeQL)
- ✅ Secret scanning (TruffleHog + GitHub native)
- ✅ Dependency vulnerabilities (Dependabot)
- ✅ AI-powered review (Copilot)
- ✅ Manual review (Code Owners)

---

**Следующий шаг:** Запустите `/tmp/github-setup-commands.sh` для автоматической настройки branch protection.
