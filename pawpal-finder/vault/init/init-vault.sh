#!/bin/sh

# Wait for Vault to be ready
echo "Waiting for Vault to be ready..."
sleep 5

# Enable KV secrets engine version 2
echo "Enabling KV secrets engine..."
vault secrets enable -version=2 -path=secret kv || echo "KV engine already enabled"

# Create secrets for the application
echo "Creating application secrets..."

# JWT Secret Key - Generate a secure random key
JWT_SECRET=$(openssl rand -base64 64 | tr -d '\n')
vault kv put secret/pawpal-finder/jwt \
  secret-key="$JWT_SECRET" \
  expiration-ms="18000000"

# Google API Key (replace with your actual key or use placeholder)
vault kv put secret/pawpal-finder/google \
  api-key="YOUR_GOOGLE_API_KEY_HERE"

# Database credentials
vault kv put secret/pawpal-finder/database \
  url="jdbc:postgresql://postgres:5432/pawpal_db" \
  username="pawpal_user" \
  password="pawpal_secure_password_2024"

# Create a policy for the application
echo "Creating application policy..."
vault policy write pawpal-finder-policy - <<EOF
# Allow reading secrets for pawpal-finder application
path "secret/data/pawpal-finder/*" {
  capabilities = ["read", "list"]
}

# Allow listing secrets
path "secret/metadata/pawpal-finder/*" {
  capabilities = ["list"]
}
EOF

# Enable AppRole auth method
echo "Enabling AppRole authentication..."
vault auth enable approle || echo "AppRole already enabled"

# Create an AppRole for the application
echo "Creating AppRole for pawpal-finder..."
vault write auth/approle/role/pawpal-finder \
  token_policies="pawpal-finder-policy" \
  token_ttl=1h \
  token_max_ttl=4h \
  secret_id_ttl=0

# Get RoleID and SecretID
echo "Generating RoleID and SecretID..."
ROLE_ID=$(vault read -field=role_id auth/approle/role/pawpal-finder/role-id)
SECRET_ID=$(vault write -field=secret_id -f auth/approle/role/pawpal-finder/secret-id)

# Save credentials to a file for the application to use
echo "Saving AppRole credentials..."
cat > /vault/init/approle-credentials.txt <<EOF
VAULT_ROLE_ID=$ROLE_ID
VAULT_SECRET_ID=$SECRET_ID
VAULT_ADDR=http://vault:8200
EOF

echo "================================"
echo "Vault initialization complete!"
echo "================================"
echo ""
echo "AppRole Credentials (save these securely):"
echo "ROLE_ID: $ROLE_ID"
echo "SECRET_ID: $SECRET_ID"
echo ""
echo "These credentials have been saved to: /vault/init/approle-credentials.txt"
echo ""
echo "To view secrets:"
echo "  vault kv get secret/pawpal-finder/jwt"
echo "  vault kv get secret/pawpal-finder/google"
echo "  vault kv get secret/pawpal-finder/database"
echo ""
echo "================================"

# List all secrets for verification
echo "Verifying secrets..."
vault kv list secret/pawpal-finder/

echo "Initialization script completed successfully!"
