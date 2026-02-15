# Git Flow for Development

## Branch Structure
- **main**: Production-ready code (releases only)
- **develop**: Integration branch for features
- **feature/phase-X-***: Feature branches

## Workflow

1. Create feature branch from develop
   ```bash
   git checkout -b feature/phase-1-setup develop
   ```

2. Make commits with meaningful messages
   ```bash
   git commit -m "feat: task description"
   ```

3. Push to GitHub
   ```bash
   git push origin feature/phase-1-setup
   ```

4. GitHub automatically creates PR
   - Branch protection activates
   - CI/CD workflows start

5. Pre-commit hooks prevent bad commits
   - If checks fail: fix locally, retry

6. GitHub Actions validates fully
   - If CI fails: fix, push again
   - Auto-comments with results

7. Code review by Claude
   - Wait for CI ✅
   - Manual review
   - Approve if good

8. Merge to develop
   - All checks passed ✅
   - All reviews approved ✅
   - Merge button enabled

## Protection Rules

**main branch:**
- ✅ Requires all CI checks pass
- ✅ Requires Security scan pass
- ✅ Requires 1 approval
- ✅ Requires code owner review
- ✅ No force push
- ✅ No direct commits

**develop branch:**
- ✅ Requires Quality + Tests pass
- ✅ No force push
- ✅ No direct commits

## What You Cannot Do

❌ Direct commit to main/develop
❌ Force push to any branch
❌ Merge without passing CI
❌ Merge without approvals
❌ Commit without pre-commit checks

## Emergency Override (if needed)

Only administrator can bypass, requires explicit approval.
For MVP: never needed - all checks are designed to pass.
