#!/bin/sh

echo "Waiting for Vault to be ready..."
sleep 5

echo "Enabling KV secrets engine..."
vault secrets enable -version=2 -path=secret kv || echo "KV engine already enabled"

echo "Creating application secrets..."

vault kv put secret/pawpal-finder/jwt \
  secret.key="ThisIsASecureRandomJWTSecretKeyGeneratedForVaultDemo123456789" \
  jwt.expirationMs="18000000"

vault kv put secret/pawpal-finder/google \
  google.api.key="AIzaSyDt5jydbk2bY4ft-KS1xJyT9t4Iua3H1wI"

vault kv put secret/pawpal-finder/database \
  spring.datasource.url="jdbc:postgresql://localhost:5432/licenta" \
  spring.datasource.username="postgres" \
  spring.datasource.password="CHANGE_THIS_PASSWORD"

echo "Creating application policy..."
cat > /tmp/policy.hcl << EOF
path "secret/data/pawpal-finder" {
  capabilities = ["read", "list"]
}
path "secret/data/pawpal-finder/*" {
  capabilities = ["read", "list"]
}
path "secret/metadata/pawpal-finder" {
  capabilities = ["list"]
}
path "secret/metadata/pawpal-finder/*" {
  capabilities = ["list"]
}
EOF
vault policy write pawpal-finder-policy /tmp/policy.hcl

echo "Enabling AppRole authentication..."
vault auth enable approle || echo "AppRole already enabled"

echo "Creating AppRole for pawpal-finder..."
vault write auth/approle/role/pawpal-finder \
  token_policies="pawpal-finder-policy" \
  token_ttl=1h \
  token_max_ttl=4h \
  secret_id_ttl=0

echo "Generating RoleID and SecretID..."
ROLE_ID=$(vault read -field=role_id auth/approle/role/pawpal-finder/role-id)
SECRET_ID=$(vault write -field=secret_id -f auth/approle/role/pawpal-finder/secret-id)

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

echo "Verifying secrets..."
vault kv list secret/pawpal-finder/

echo "Initialization script completed successfully!"