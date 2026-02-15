# GitHub Actions CI/CD Pipeline Configuration

## Workflows Overview

### 1. Quality Checks
- **Trigger**: On PR creation/update, push to develop/main
- **Jobs**: Build, ktlint, detekt
- **Duration**: ~5 minutes
- **Purpose**: Ensure code quality standards

### 2. Tests
- **Trigger**: On PR creation/update, push to develop/main
- **Jobs**: Unit tests, integration tests
- **Duration**: ~5 minutes
- **Artifacts**: Test reports, coverage data
- **Purpose**: Verify functionality

### 3. Security Scanning
- **Trigger**: On PR creation/update, daily schedule
- **Jobs**: Secret detection, dependency check
- **Duration**: ~3 minutes
- **Purpose**: Prevent security vulnerabilities

## Status Checks Required

For **main** branch:
- ✅ Quality Checks (all jobs pass)
- ✅ Tests (all tests pass)
- ✅ Security Scanning (no issues)

For **develop** branch:
- ✅ Quality Checks (all jobs pass)
- ✅ Tests (all tests pass)

## PR Workflow

1. Create feature branch from develop
2. Push commits (CI runs automatically)
3. Check CI results in PR
4. If any check fails, fix and push again
5. All checks must pass before merge
6. Code owner approval required
7. Merge to develop

## Debugging CI Failures

### Quality Checks Failed
```bash
# Run locally
./gradlew build -x test
./gradlew ktlintCheck
./gradlew detekt
```

### Tests Failed
```bash
# Run locally
./gradlew test
```

### Security Scan Failed
```bash
# Check for secrets
git log --all --full-history -- '*secrets*'
git log --all --full-history -S 'password'
```
