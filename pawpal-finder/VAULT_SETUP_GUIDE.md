# HashiCorp Vault Setup Guide - PawPal Finder

## Table of Contents
1. [Overview](#overview)
2. [Prerequisites](#prerequisites)
3. [Quick Start](#quick-start)
4. [Detailed Setup](#detailed-setup)
5. [Configuration](#configuration)
6. [Running the Application](#running-the-application)
7. [Managing Secrets](#managing-secrets)
8. [Troubleshooting](#troubleshooting)
9. [Production Considerations](#production-considerations)

---

## Overview

This guide explains how to set up HashiCorp Vault for secrets management in the PawPal Finder application. Vault replaces hard-coded credentials with secure, centralized secret storage.

**What's Included:**
- HashiCorp Vault server (running in Docker)
- PostgreSQL database (running in Docker)
- Automated Vault initialization
- Spring Cloud Vault integration
- AppRole authentication

---

## Prerequisites

Before starting, ensure you have:

- **Docker Desktop** installed and running
- **Docker Compose** installed (usually comes with Docker Desktop)
- **Java 21** or higher
- **Maven** (or use the included `mvnw` wrapper)
- **Git** (for version control)

### Verify Prerequisites

```bash
# Check Docker
docker --version
docker-compose --version

# Check Java
java -version

# Check Maven
mvn --version
# OR use the wrapper
./mvnw --version
```

---

## Quick Start

For a fast setup, run the automated script:

```bash
# Navigate to the project directory
cd pawpal-finder

# Run the setup script
./setup-vault.sh
```

The script will:
1. Start Vault and PostgreSQL containers
2. Initialize Vault with secrets
3. Create AppRole credentials
4. Display configuration instructions

**Save the AppRole credentials** displayed at the end - you'll need them to run the application!

---

## Detailed Setup

### Step 1: Start Infrastructure

```bash
# Start Vault and PostgreSQL
docker-compose up -d vault postgres

# Verify containers are running
docker ps
```

You should see:
- `pawpal-vault` (port 8200)
- `pawpal-postgres` (port 5432)

### Step 2: Initialize Vault

```bash
# Run the initialization container
docker-compose up vault-init

# Check the logs
docker-compose logs vault-init
```

This creates:
- KV secrets engine at `secret/`
- Application secrets (JWT, Google API, Database)
- AppRole authentication
- Security policies

### Step 3: Retrieve AppRole Credentials

```bash
# View the credentials file
cat vault/init/approle-credentials.txt
```

You'll see:
```
VAULT_ROLE_ID=xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx
VAULT_SECRET_ID=xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx
VAULT_ADDR=http://vault:8200
```

### Step 4: Configure Environment Variables

**Option A: Export in Terminal**
```bash
export VAULT_ROLE_ID=your-role-id-here
export VAULT_SECRET_ID=your-secret-id-here
```

**Option B: Create .env File** (Recommended)
```bash
# Create .env file (DO NOT commit to Git!)
cat > .env <<EOF
VAULT_ROLE_ID=your-role-id-here
VAULT_SECRET_ID=your-secret-id-here
EOF
```

### Step 5: Update Google API Key

Replace the placeholder with your actual Google Maps API key:

```bash
docker exec -e VAULT_TOKEN=dev-root-token pawpal-vault \
  vault kv put secret/pawpal-finder/google \
  api-key='YOUR_ACTUAL_GOOGLE_API_KEY'
```

---

## Configuration

### Vault Configuration Files

**bootstrap.properties** - Loaded first, configures Vault connection
```properties
spring.cloud.vault.enabled=true
spring.cloud.vault.uri=http://localhost:8200
spring.cloud.vault.authentication=APPROLE
spring.cloud.vault.app-role.role-id=${VAULT_ROLE_ID}
spring.cloud.vault.app-role.secret-id=${VAULT_SECRET_ID}
```

**application-vault.properties** - Application configuration with Vault
```properties
# Secrets are automatically injected from Vault
secret.key=${spring.cloud.vault.kv.pawpal-finder.jwt.secret-key}
google.api.key=${spring.cloud.vault.kv.pawpal-finder.google.api-key}
```

### Secrets Structure in Vault

```
secret/
└── pawpal-finder/
    ├── jwt/
    │   ├── secret-key (auto-generated secure key)
    │   └── expiration-ms (18000000)
    ├── google/
    │   └── api-key (your Google Maps API key)
    └── database/
        ├── url (jdbc:postgresql://postgres:5432/pawpal_db)
        ├── username (pawpal_user)
        └── password (pawpal_secure_password_2024)
```

---

## Running the Application

### Method 1: Using Maven with Profile

```bash
# Set environment variables
export VAULT_ROLE_ID=your-role-id
export VAULT_SECRET_ID=your-secret-id

# Run with vault profile
./mvnw spring-boot:run -Dspring-boot.run.profiles=vault
```

### Method 2: Using IDE (IntelliJ IDEA / Eclipse)

1. Open Run Configuration
2. Add VM Options: `-Dspring.profiles.active=vault`
3. Add Environment Variables:
   - `VAULT_ROLE_ID=your-role-id`
   - `VAULT_SECRET_ID=your-secret-id`
4. Run the application

### Method 3: Using JAR

```bash
# Build the application
./mvnw clean package -DskipTests

# Run with environment variables
export VAULT_ROLE_ID=your-role-id
export VAULT_SECRET_ID=your-secret-id

java -jar -Dspring.profiles.active=vault target/pawpal-finder-0.0.1-SNAPSHOT.jar
```

### Verify Application Started Successfully

Look for these log messages:
```
HashiCorp Vault Configuration Initialized
Vault URI: http://localhost:8200
Secrets will be loaded from: secret/data/pawpal-finder/*
```

---

## Managing Secrets

### View Secrets

```bash
# View JWT secrets
docker exec -e VAULT_TOKEN=dev-root-token pawpal-vault \
  vault kv get secret/pawpal-finder/jwt

# View Google API secrets
docker exec -e VAULT_TOKEN=dev-root-token pawpal-vault \
  vault kv get secret/pawpal-finder/google

# View Database secrets
docker exec -e VAULT_TOKEN=dev-root-token pawpal-vault \
  vault kv get secret/pawpal-finder/database
```

### Update Secrets

```bash
# Update JWT expiration
docker exec -e VAULT_TOKEN=dev-root-token pawpal-vault \
  vault kv patch secret/pawpal-finder/jwt \
  expiration-ms="36000000"

# Update Google API key
docker exec -e VAULT_TOKEN=dev-root-token pawpal-vault \
  vault kv patch secret/pawpal-finder/google \
  api-key="new-api-key-here"

# Update database password
docker exec -e VAULT_TOKEN=dev-root-token pawpal-vault \
  vault kv patch secret/pawpal-finder/database \
  password="new-secure-password"
```

### Rotate Secrets

```bash
# Generate new JWT secret
NEW_JWT_SECRET=$(openssl rand -base64 64 | tr -d '\n')
docker exec -e VAULT_TOKEN=dev-root-token pawpal-vault \
  vault kv patch secret/pawpal-finder/jwt \
  secret-key="$NEW_JWT_SECRET"

# Restart application to pick up new secret
```

### Access Vault UI

1. Open browser: http://localhost:8200
2. Login with token: `dev-root-token`
3. Navigate to: `secret/pawpal-finder/`

---

## Troubleshooting

### Issue: Vault Connection Failed

**Symptoms:**
```
Failed to configure from vault: Connection refused
```

**Solution:**
```bash
# Check if Vault is running
docker ps | grep vault

# Check Vault logs
docker logs pawpal-vault

# Restart Vault
docker-compose restart vault
```

### Issue: Authentication Failed

**Symptoms:**
```
permission denied
```

**Solution:**
```bash
# Verify environment variables are set
echo $VAULT_ROLE_ID
echo $VAULT_SECRET_ID

# Regenerate AppRole credentials
docker exec -e VAULT_TOKEN=dev-root-token pawpal-vault \
  vault write -field=secret_id -f auth/approle/role/pawpal-finder/secret-id
```

### Issue: Secrets Not Found

**Symptoms:**
```
Could not locate PropertySource: 404 Not Found
```

**Solution:**
```bash
# Verify secrets exist
docker exec -e VAULT_TOKEN=dev-root-token pawpal-vault \
  vault kv list secret/pawpal-finder/

# Re-run initialization
docker-compose up vault-init
```

### Issue: Database Connection Failed

**Symptoms:**
```
Connection to localhost:5432 refused
```

**Solution:**
```bash
# Check PostgreSQL is running
docker ps | grep postgres

# Check PostgreSQL logs
docker logs pawpal-postgres

# Restart PostgreSQL
docker-compose restart postgres
```

### Common Commands

```bash
# View all containers
docker-compose ps

# View logs
docker-compose logs -f vault
docker-compose logs -f postgres

# Restart services
docker-compose restart

# Stop all services
docker-compose down

# Stop and remove volumes (clean slate)
docker-compose down -v
```

---

## Production Considerations

### ⚠️ Important: This is a Development Setup

The current configuration uses Vault in **dev mode** which is NOT suitable for production.

### Production Checklist

#### 1. Use Production Vault Mode

```yaml
# docker-compose-prod.yml
vault:
  image: hashicorp/vault:1.15
  command: server
  environment:
    VAULT_LOCAL_CONFIG: |
      storage "file" {
        path = "/vault/data"
      }
      listener "tcp" {
        address = "0.0.0.0:8200"
        tls_disable = 1
      }
```

#### 2. Enable TLS/SSL

- Use HTTPS for Vault communication
- Configure proper certificates
- Update `spring.cloud.vault.uri` to use `https://`

#### 3. Secure Root Token

- Initialize Vault properly (not dev mode)
- Store unseal keys securely (use Shamir's Secret Sharing)
- Rotate root token regularly
- Use limited-privilege tokens for applications

#### 4. Use Dynamic Database Credentials

```bash
# Enable database secrets engine
vault secrets enable database

# Configure PostgreSQL connection
vault write database/config/postgresql \
  plugin_name=postgresql-database-plugin \
  connection_url="postgresql://{{username}}:{{password}}@postgres:5432/pawpal_db" \
  allowed_roles="pawpal-role" \
  username="vault_admin" \
  password="vault_admin_password"
```

#### 5. Implement Secret Rotation

- Set up automatic secret rotation policies
- Configure TTL (Time To Live) for secrets
- Implement graceful secret refresh in application

#### 6. Enable Audit Logging

```bash
vault audit enable file file_path=/vault/logs/audit.log
```

#### 7. Network Security

- Use private networks for Vault
- Implement firewall rules
- Use VPN or private connectivity
- Never expose Vault directly to the internet

#### 8. Backup and Disaster Recovery

- Regular backups of Vault data
- Test restore procedures
- Document recovery processes
- Store backups securely and encrypted

#### 9. Monitoring and Alerting

- Monitor Vault health
- Set up alerts for:
  - Failed authentication attempts
  - Secret access patterns
  - Service availability
  - Token expiration

#### 10. Compliance

- Enable audit logging
- Implement access controls
- Regular security audits
- Document all procedures

### Production Environment Variables

```bash
# Production .env (store securely, never commit)
VAULT_ADDR=https://vault.production.company.com
VAULT_ROLE_ID=prod-role-id
VAULT_SECRET_ID=prod-secret-id
VAULT_NAMESPACE=production
```

---

## Additional Resources

- [HashiCorp Vault Documentation](https://www.vaultproject.io/docs)
- [Spring Cloud Vault](https://spring.io/projects/spring-cloud-vault)
- [Vault Best Practices](https://www.vaultproject.io/docs/internals/security)
- [AppRole Authentication](https://www.vaultproject.io/docs/auth/approle)

---

## Support

For issues or questions:
1. Check the [Troubleshooting](#troubleshooting) section
2. Review Vault logs: `docker logs pawpal-vault`
3. Review application logs
4. Consult the official documentation

---

*Last Updated: 2026-05-17*  
*Version: 1.0*