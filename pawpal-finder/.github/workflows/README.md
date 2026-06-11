# GitHub Actions CI Workflow

This directory contains the CI workflow for the PawPal Finder backend application.

## CI Pipeline (`ci.yml`)

**Triggers:**
- Push to `main` or `develop` branches
- Pull requests to `main` or `develop` branches

**What it does:**
1. Checks out the code
2. Sets up Java 21 (Temurin distribution)
3. Caches Maven dependencies for faster builds
4. Runs `mvn clean test` to build and test the application
5. Uploads test results as artifacts (available for 90 days)

## Viewing Results

After a workflow run:
1. Go to the "Actions" tab in your GitHub repository
2. Click on the workflow run you want to see
3. View the test results in the job logs
4. Download test artifacts if needed

## Running Tests Locally

Before pushing, run tests locally:

```bash
cd pawpal-finder
mvn clean test
```

## Status Badge

The README includes a status badge showing the current build status:

```markdown
[![CI Pipeline](https://github.com/carinus2/licenta-back/actions/workflows/ci.yml/badge.svg)](https://github.com/carinus2/licenta-back/actions/workflows/ci.yml)