# GitHub Actions Workflows

This directory contains the CI/CD workflows for the PawPal Finder backend application.

## Available Workflows

### 1. CI Pipeline (`ci.yml`)

**Triggers:**
- Push to `main` or `develop` branches
- Pull requests to `main` or `develop` branches

**Jobs:**

#### Test Job
- Sets up Java 21 (Temurin distribution)
- Configures PostgreSQL service container for integration tests
- Caches Maven dependencies for faster builds
- Compiles the application
- Runs all unit and integration tests
- Generates test reports
- Uploads test results and coverage reports as artifacts

#### Lint Job
- Runs Maven verify to check code quality
- Validates code formatting
- Runs independently from test job

#### Build Job
- Depends on successful completion of test and lint jobs
- Creates the application JAR package
- Uploads the JAR as an artifact (retained for 7 days)

### 2. Code Quality (`code-quality.yml`)

**Triggers:**
- Push to `main` or `develop` branches
- Pull requests to `main` or `develop` branches

**Jobs:**

#### Checkstyle Analysis
- Runs Checkstyle to enforce Java coding standards
- Uses custom `checkstyle.xml` configuration
- Generates detailed reports
- Uploads results as artifacts

#### Dependency Check
- Analyzes project dependencies
- Checks for unused or problematic dependencies
- Generates dependency tree for review

## Artifacts

All workflows upload artifacts that can be downloaded from the Actions tab:

- **test-results**: Surefire test reports
- **coverage-reports**: JaCoCo code coverage reports
- **checkstyle-results**: Checkstyle analysis results
- **application-jar**: Built application JAR file

## Local Testing

Before pushing, you can run the same checks locally:

```bash
# Run tests
mvn clean test

# Run with coverage
mvn clean test jacoco:report

# Check code style
mvn checkstyle:check

# Full build
mvn clean package

# Verify everything
mvn clean verify
```

## Configuration Files

- `pom.xml`: Maven configuration with plugins for testing, coverage, and code quality
- `checkstyle.xml`: Custom Checkstyle rules for code quality
- `src/test/resources/application-test.properties`: Test-specific configuration

## Status Badges

Add these badges to your README to show build status:

```markdown
[![CI Pipeline](https://github.com/carinus2/licenta-back/actions/workflows/ci.yml/badge.svg)](https://github.com/carinus2/licenta-back/actions/workflows/ci.yml)
[![Code Quality](https://github.com/carinus2/licenta-back/actions/workflows/code-quality.yml/badge.svg)](https://github.com/carinus2/licenta-back/actions/workflows/code-quality.yml)
```

## Troubleshooting

### Tests Failing in CI but Passing Locally

- Ensure you're using Java 21
- Check that test database configuration matches CI setup
- Verify all environment variables are properly set

### Checkstyle Violations

- Run `mvn checkstyle:check` locally to see violations
- Fix violations or adjust `checkstyle.xml` if rules are too strict
- Checkstyle is configured to warn, not fail the build

### Build Artifacts Not Available

- Artifacts are retained for 7 days by default
- Check the Actions tab for download links
- Ensure the build job completed successfully