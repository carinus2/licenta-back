# Testing Guide - HashiCorp Vault Implementation

## 🧪 Complete Testing Instructions

This guide will walk you through testing the HashiCorp Vault secrets management implementation step by step.

---

## Prerequisites Check

Before starting, verify you have everything installed:

```bash
# Check Docker
docker --version
# Expected: Docker version 20.x or higher

# Check Docker Compose
docker-compose --version
# Expected: Docker Compose version 2.x or higher

# Check Java
java -version
# Expected: Java 21 or higher

# Check Maven
./mvnw --version
# Expected: Maven 3.x or higher
```

---

## Step 1: Start Docker Desktop

1. **Open Docker Desktop application**
   - On macOS: Open from Applications folder
   - Wait until Docker icon in menu bar shows "Docker Desktop is running"

2. **Verify Docker is running**
   ```bash
   docker ps
   ```
   - Should show an empty list or running containers (no error)

---

## Step 2: Run the Automated Setup

Navigate to your project directory and run the setup script:

```bash
cd /Users/carinanistor/Desktop/faculta/licenta-back/pawpal-finder

# Run the setup script
./setup-vault.sh
```

### Expected Output:

```
==========================================
PawPal Finder - Vault Setup
==========================================

✓ Docker is running
✓ docker-compose is available

ℹ Step 1: Cleaning up existing containers...
✓ Cleanup complete

ℹ Step 2: Creating necessary directories...
✓ Directories created

ℹ Step 3: Starting Vault and PostgreSQL containers...
✓ Containers started

ℹ Step 4: Waiting for services to be healthy...
✓ Vault is ready
✓ PostgreSQL is ready

ℹ Step 5: Initializing Vault with secrets...
✓ Vault initialization complete

ℹ Step 6: Extracting AppRole credentials...
✓ AppRole credentials loaded

==========================================
Vault Setup Complete!
==========================================

AppRole Credentials (save these securely):
VAULT_ROLE_ID=xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx
VAULT_SECRET_ID=xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx
```

### ⚠️ IMPORTANT: Save Your Credentials!

Copy the `VAULT_ROLE_ID` and `VAULT_SECRET_ID` values - you'll need them to run the application!

---

## Step 3: Verify Vault is Running

### Check Container Status

```bash
docker ps
```

**Expected output:**
```
CONTAINER ID   IMAGE                    STATUS         PORTS                    NAMES
xxxxxxxxxx     hashicorp/vault:1.15     Up 2 minutes   0.0.0.0:8200->8200/tcp   pawpal-vault
xxxxxxxxxx     postgres:15-alpine       Up 2 minutes   0.0.0.0:5432->5432/tcp   pawpal-postgres
```

### Check Vault Health

```bash
docker exec pawpal-vault vault status
```

**Expected output:**
```
Key             Value
---             -----
Seal Type       shamir
Initialized     true
Sealed          false
Total Shares    1
Threshold       1
Version         1.15.x
Storage Type    inmem
Cluster Name    vault-cluster-xxxxx
Cluster ID      xxxxx-xxxxx-xxxxx
HA Enabled      false
```

---

## Step 4: Verify Secrets in Vault

### View JWT Secrets

```bash
docker exec -e VAULT_TOKEN=dev-root-token pawpal-vault \
  vault kv get secret/pawpal-finder/jwt
```

**Expected output:**
```
====== Data ======
Key              Value
---              -----
expiration-ms    18000000
secret-key       [long base64 string - auto-generated secure key]
```

### View Google API Secrets

```bash
docker exec -e VAULT_TOKEN=dev-root-token pawpal-vault \
  vault kv get secret/pawpal-finder/google
```

**Expected output:**
```
====== Data ======
Key        Value
---        -----
api-key    YOUR_GOOGLE_API_KEY_HERE
```

### View Database Secrets

```bash
docker exec -e VAULT_TOKEN=dev-root-token pawpal-vault \
  vault kv get secret/pawpal-finder/database
```

**Expected output:**
```
====== Data ======
Key         Value
---         -----
password    pawpal_secure_password_2024
url         jdbc:postgresql://postgres:5432/pawpal_db
username    pawpal_user
```

---

## Step 5: Update Google API Key (Optional but Recommended)

Replace the placeholder with your actual Google Maps API key:

```bash
docker exec -e VAULT_TOKEN=dev-root-token pawpal-vault \
  vault kv put secret/pawpal-finder/google \
  api-key='YOUR_ACTUAL_GOOGLE_API_KEY'
```

**Verify the update:**
```bash
docker exec -e VAULT_TOKEN=dev-root-token pawpal-vault \
  vault kv get secret/pawpal-finder/google
```

---

## Step 6: Set Environment Variables

You need to set the AppRole credentials as environment variables. Choose one method:

### Method A: Export in Current Terminal (Temporary)

```bash
# Replace with your actual values from Step 2
export VAULT_ROLE_ID=your-role-id-here
export VAULT_SECRET_ID=your-secret-id-here

# Verify they're set
echo $VAULT_ROLE_ID
echo $VAULT_SECRET_ID
```

### Method B: Create .env File (Recommended)

```bash
# Create .env file (this is already in .gitignore)
cat > .env <<EOF
VAULT_ROLE_ID=your-role-id-here
VAULT_SECRET_ID=your-secret-id-here
EOF

# Load the environment variables
source .env

# Verify they're set
echo $VAULT_ROLE_ID
echo $VAULT_SECRET_ID
```

---

## Step 7: Test Application Startup

### Option A: Using Maven

```bash
# Make sure you're in the project directory
cd /Users/carinanistor/Desktop/faculta/licenta-back/pawpal-finder

# Run with vault profile
./mvnw spring-boot:run -Dspring-boot.run.profiles=vault
```

### Option B: Using IntelliJ IDEA

1. Open the project in IntelliJ IDEA
2. Go to **Run → Edit Configurations**
3. Add/Edit Spring Boot configuration:
   - **VM Options**: `-Dspring.profiles.active=vault`
   - **Environment Variables**: 
     - `VAULT_ROLE_ID=your-role-id`
     - `VAULT_SECRET_ID=your-secret-id`
4. Click **Run**

### Expected Success Indicators

Look for these log messages during startup:

```
=================================================
HashiCorp Vault Configuration Initialized
=================================================
Vault URI: http://localhost:8200
KV Backend: secret
Default Context: pawpal-finder
Application Name: pawpal-finder
=================================================
Secrets will be loaded from: secret/data/pawpal-finder/*
=================================================
```

And later:

```
Started PawpalFinderApplication in X.XXX seconds
```

### ✅ Success Criteria

- Application starts without errors
- No "connection refused" errors
- No "authentication failed" errors
- Application connects to PostgreSQL successfully
- JWT secret is loaded from Vault (not the hard-coded one)

---

## Step 8: Verify Secrets Are Being Used

### Test 1: Check JWT Secret

The application should be using the auto-generated JWT secret from Vault, not the hard-coded one.

**In application logs, you should NOT see:**
```
secret.key="ThisIsTheLongestKeyEverOnThePlanetISwear!"
```

### Test 2: Check Database Connection

The application should connect to PostgreSQL using credentials from Vault.

**Look for in logs:**
```
HikariPool-1 - Starting...
HikariPool-1 - Start completed.
```

### Test 3: Test an API Endpoint

If your application is running, test an endpoint:

```bash
# Test health endpoint (if available)
curl http://localhost:8080/actuator/health

# Or test your authentication endpoint
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"test@example.com","password":"password"}'
```

---

## Step 9: Test Secret Rotation (Advanced)

This demonstrates zero-downtime secret rotation:

### Rotate JWT Secret

```bash
# Generate a new JWT secret
NEW_JWT_SECRET=$(openssl rand -base64 64 | tr -d '\n')

# Update in Vault
docker exec -e VAULT_TOKEN=dev-root-token pawpal-vault \
  vault kv patch secret/pawpal-finder/jwt \
  secret-key="$NEW_JWT_SECRET"

# Verify the update
docker exec -e VAULT_TOKEN=dev-root-token pawpal-vault \
  vault kv get secret/pawpal-finder/jwt
```

**Note:** The application will pick up the new secret on the next token refresh (within 1 hour) or on restart.

---

## Step 10: Access Vault UI (Optional)

1. Open your browser and go to: **http://localhost:8200**
2. Login with token: `dev-root-token`
3. Navigate to: **secret → pawpal-finder**
4. You can view and manage secrets through the UI

---

## Troubleshooting

### Issue 1: Docker Not Running

**Error:**
```
✗ Docker is not running. Please start Docker and try again.
```

**Solution:**
1. Open Docker Desktop
2. Wait for it to fully start
3. Run `docker ps` to verify
4. Re-run `./setup-vault.sh`

---

### Issue 2: Port Already in Use

**Error:**
```
Error: port 8200 is already allocated
```

**Solution:**
```bash
# Stop existing containers
docker-compose down

# Or stop specific container using the port
docker ps
docker stop <container-id>

# Re-run setup
./setup-vault.sh
```

---

### Issue 3: Vault Connection Failed

**Error in application logs:**
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

# Wait 10 seconds and try again
```

---

### Issue 4: Authentication Failed

**Error:**
```
permission denied
```

**Solution:**
```bash
# Verify environment variables are set
echo $VAULT_ROLE_ID
echo $VAULT_SECRET_ID

# If empty, set them again
export VAULT_ROLE_ID=your-role-id
export VAULT_SECRET_ID=your-secret-id

# Or regenerate credentials
docker exec -e VAULT_TOKEN=dev-root-token pawpal-vault \
  vault write -field=secret_id -f auth/approle/role/pawpal-finder/secret-id
```

---

### Issue 5: Secrets Not Found

**Error:**
```
Could not locate PropertySource: 404 Not Found
```

**Solution:**
```bash
# Verify secrets exist
docker exec -e VAULT_TOKEN=dev-root-token pawpal-vault \
  vault kv list secret/pawpal-finder/

# If empty, re-run initialization
docker-compose up vault-init
```

---

## Clean Up (When Done Testing)

### Stop All Services

```bash
docker-compose down
```

### Stop and Remove All Data (Clean Slate)

```bash
docker-compose down -v
```

This removes all containers, networks, and volumes. You'll need to run `./setup-vault.sh` again to start fresh.

---

## Testing Checklist

Use this checklist to verify everything works:

- [ ] Docker Desktop is running
- [ ] `./setup-vault.sh` completed successfully
- [ ] AppRole credentials saved
- [ ] Vault container is running (`docker ps`)
- [ ] PostgreSQL container is running (`docker ps`)
- [ ] Secrets visible in Vault (JWT, Google, Database)
- [ ] Environment variables set (`VAULT_ROLE_ID`, `VAULT_SECRET_ID`)
- [ ] Application starts with vault profile
- [ ] No connection errors in logs
- [ ] Vault configuration messages appear in logs
- [ ] Application connects to database
- [ ] API endpoints respond correctly
- [ ] Vault UI accessible at http://localhost:8200

---

## Next Steps After Testing

1. **Document Your Results**: Take screenshots of successful startup
2. **Test Secret Rotation**: Try rotating a secret and verify it works
3. **Review Documentation**: Read `SECURITY_ANALYSIS.md` for the complete before/after comparison
4. **Prepare Presentation**: Use the documentation for your DevOps project presentation

---

## Quick Reference Commands

```bash
# Start everything
./setup-vault.sh

# Check status
docker ps
docker logs pawpal-vault
docker logs pawpal-postgres

# View secrets
docker exec -e VAULT_TOKEN=dev-root-token pawpal-vault vault kv get secret/pawpal-finder/jwt

# Run application
export VAULT_ROLE_ID=your-role-id
export VAULT_SECRET_ID=your-secret-id
./mvnw spring-boot:run -Dspring-boot.run.profiles=vault

# Stop everything
docker-compose down

# Clean slate
docker-compose down -v
```

---

## Support

If you encounter issues not covered here:
1. Check the [VAULT_SETUP_GUIDE.md](VAULT_SETUP_GUIDE.md) for detailed troubleshooting
2. Review Docker logs: `docker logs pawpal-vault`
3. Check application logs for specific error messages

---

*Good luck with your testing! 🚀*

*Last Updated: May 17, 2026*