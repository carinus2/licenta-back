# How to View Secrets in Vault

## Method 1: Command Line (Quick)

### List all secret paths:
```bash
docker exec -e VAULT_TOKEN=dev-root-token pawpal-vault vault kv list secret/pawpal-finder/
```

**Expected output:**
```
Keys
----
database
google
jwt
```

### View JWT secrets:
```bash
docker exec -e VAULT_TOKEN=dev-root-token pawpal-vault vault kv get secret/pawpal-finder/jwt
```

### View Google API secrets:
```bash
docker exec -e VAULT_TOKEN=dev-root-token pawpal-vault vault kv get secret/pawpal-finder/google
```

### View Database secrets:
```bash
docker exec -e VAULT_TOKEN=dev-root-token pawpal-vault vault kv get secret/pawpal-finder/database
```

### View ALL secrets at once:
```bash
echo "=== JWT Secrets ==="
docker exec -e VAULT_TOKEN=dev-root-token pawpal-vault vault kv get secret/pawpal-finder/jwt

echo -e "\n=== Google API Secrets ==="
docker exec -e VAULT_TOKEN=dev-root-token pawpal-vault vault kv get secret/pawpal-finder/google

echo -e "\n=== Database Secrets ==="
docker exec -e VAULT_TOKEN=dev-root-token pawpal-vault vault kv get secret/pawpal-finder/database
```

---

## Method 2: Vault Web UI (Visual)

1. **Open your browser** and go to: http://localhost:8200

2. **Login** with token: `dev-root-token`

3. **Navigate** to: `secret` → `pawpal-finder`

4. **Click** on any secret (jwt, google, database) to view its contents

---

## Method 3: View Specific Keys Only

### Get just the JWT secret key:
```bash
docker exec -e VAULT_TOKEN=dev-root-token pawpal-vault \
  vault kv get -field=secret.key secret/pawpal-finder/jwt
```

### Get just the database URL:
```bash
docker exec -e VAULT_TOKEN=dev-root-token pawpal-vault \
  vault kv get -field=url secret/pawpal-finder/database
```

### Get just the Google API key:
```bash
docker exec -e VAULT_TOKEN=dev-root-token pawpal-vault \
  vault kv get -field=api-key secret/pawpal-finder/google
```

---

## What You Currently Have in Vault

Based on your setup, you should see:

### secret/pawpal-finder/jwt
```
Key                Value
---                -----
secret.key         (empty - needs to be added)
jwt.expirationMs   (empty - needs to be added)
```

### secret/pawpal-finder/google
```
Key            Value
---            -----
api-key        (empty - needs to be added)
```

### secret/pawpal-finder/database
```
Key         Value
---         -----
password    pawpal_secure_password_2024
url         jdbc:postgresql://postgres:5432/pawpal_db
username    pawpal_user
```

**Note:** The database secrets have the WRONG key names. They should be:
- `spring.datasource.url` (not `url`)
- `spring.datasource.username` (not `username`)
- `spring.datasource.password` (not `password`)

---

## Fix Your Vault Secrets

Run these commands to fix everything:

```bash
# 1. Fix database secrets with YOUR actual credentials
docker exec -e VAULT_TOKEN=dev-root-token pawpal-vault vault kv put secret/pawpal-finder/database \
  spring.datasource.url="jdbc:postgresql://localhost:5432/licenta" \
  spring.datasource.username="postgres" \
  spring.datasource.password="YOUR_POSTGRES_PASSWORD"

# 2. Add JWT secrets
docker exec -e VAULT_TOKEN=dev-root-token pawpal-vault vault kv put secret/pawpal-finder/jwt \
  secret.key="ThisIsASecureRandomJWTSecretKeyGeneratedForVaultDemo123456789" \
  jwt.expirationMs="18000000"

# 3. Add Google API key
docker exec -e VAULT_TOKEN=dev-root-token pawpal-vault vault kv put secret/pawpal-finder/google \
  google.api.key="AIzaSyDt5jydbk2bY4ft-KS1xJyT9t4Iua3H1wI"
```

---

## Verify Everything is Correct

After running the fix commands, verify:

```bash
# Should show spring.datasource.url, spring.datasource.username, spring.datasource.password
docker exec -e VAULT_TOKEN=dev-root-token pawpal-vault vault kv get secret/pawpal-finder/database

# Should show secret.key and jwt.expirationMs
docker exec -e VAULT_TOKEN=dev-root-token pawpal-vault vault kv get secret/pawpal-finder/jwt

# Should show google.api.key
docker exec -e VAULT_TOKEN=dev-root-token pawpal-vault vault kv get secret/pawpal-finder/google
```

---

## Then Run Your Application

```bash
export VAULT_ROLE_ID=2b9c22bc-5b19-d780-d9a8-d4a5d15b43d9
export VAULT_SECRET_ID=14e806bf-e719-0004-13a4-9ebba45b31b5
./mvnw spring-boot:run -Dspring-boot.run.profiles=vault
