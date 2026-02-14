#!/bin/bash
# scripts/setup-pre-commit-hooks.sh

set -e

echo "Setting up pre-commit hooks..."

# Install pre-commit if not present
if ! command -v pre-commit &> /dev/null; then
    echo "Installing pre-commit..."
    pip install pre-commit
fi

# Install hooks
pre-commit install

echo "✓ Pre-commit hooks installed"
echo "✓ Code quality checks will run before each commit"
