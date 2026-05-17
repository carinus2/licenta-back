#!/bin/bash

# PawPal Finder - HashiCorp Vault Setup Script
# This script sets up the complete Vault infrastructure for secrets management

set -e

echo "=========================================="
echo "PawPal Finder - Vault Setup"
echo "=========================================="
echo ""

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Function to print colored output
print_success() {
    echo -e "${GREEN}✓ $1${NC}"
}

print_error() {
    echo -e "${RED}✗ $1${NC}"
}

print_info() {
    echo -e "${YELLOW}ℹ $1${NC}"
}

# Check if Docker is running
if ! docker info > /dev/null 2>&1; then
    print_error "Docker is not running. Please start Docker and try again."
    exit 1
fi
print_success "Docker is running"

# Check if docker-compose is available
if ! command -v docker-compose &> /dev/null; then
    print_error "docker-compose is not installed. Please install it and try again."
    exit 1
fi
print_success "docker-compose is available"

echo ""
print_info "Step 1: Cleaning up existing containers..."
docker-compose down -v 2>/dev/null || true
print_success "Cleanup complete"

echo ""
print_info "Step 2: Creating necessary directories..."
mkdir -p vault/config vault/data vault/logs vault/init
print_success "Directories created"

echo ""
print_info "Step 3: Starting Vault and PostgreSQL containers..."
docker-compose up -d vault postgres
print_success "Containers started"

echo ""
print_info "Step 4: Waiting for services to be healthy..."
sleep 10

# Wait for Vault to be ready
MAX_RETRIES=30
RETRY_COUNT=0
while [ $RETRY_COUNT -lt $MAX_RETRIES ]; do
    if docker exec pawpal-vault vault status > /dev/null 2>&1; then
        print_success "Vault is ready"
        break
    fi
    RETRY_COUNT=$((RETRY_COUNT + 1))
    echo "Waiting for Vault... ($RETRY_COUNT/$MAX_RETRIES)"
    sleep 2
done

if [ $RETRY_COUNT -eq $MAX_RETRIES ]; then
    print_error "Vault failed to start"
    exit 1
fi

# Wait for PostgreSQL to be ready
RETRY_COUNT=0
while [ $RETRY_COUNT -lt $MAX_RETRIES ]; do
    if docker exec pawpal-postgres pg_isready -U pawpal_user > /dev/null 2>&1; then
        print_success "PostgreSQL is ready"
        break
    fi
    RETRY_COUNT=$((RETRY_COUNT + 1))
    echo "Waiting for PostgreSQL... ($RETRY_COUNT/$MAX_RETRIES)"
    sleep 2
done

if [ $RETRY_COUNT -eq $MAX_RETRIES ]; then
    print_error "PostgreSQL failed to start"
    exit 1
fi

echo ""
print_info "Step 5: Initializing Vault with secrets..."
docker-compose up vault-init
print_success "Vault initialization complete"

echo ""
print_info "Step 6: Extracting AppRole credentials..."
if [ -f "vault/init/approle-credentials.txt" ]; then
    source vault/init/approle-credentials.txt
    print_success "AppRole credentials loaded"
    
    echo ""
    echo "=========================================="
    echo "Vault Setup Complete!"
    echo "=========================================="
    echo ""
    echo "AppRole Credentials (save these securely):"
    echo "VAULT_ROLE_ID=$VAULT_ROLE_ID"
    echo "VAULT_SECRET_ID=$VAULT_SECRET_ID"
    echo ""
    echo "To use these credentials, export them as environment variables:"
    echo "  export VAULT_ROLE_ID=$VAULT_ROLE_ID"
    echo "  export VAULT_SECRET_ID=$VAULT_SECRET_ID"
    echo ""
    echo "Or create a .env file (DO NOT commit to Git):"
    echo "  echo 'VAULT_ROLE_ID=$VAULT_ROLE_ID' > .env"
    echo "  echo 'VAULT_SECRET_ID=$VAULT_SECRET_ID' >> .env"
    echo ""
    echo "Vault UI: http://localhost:8200"
    echo "Root Token (dev mode): dev-root-token"
    echo ""
    echo "To view secrets in Vault:"
    echo "  docker exec pawpal-vault vault kv get secret/pawpal-finder/jwt"
    echo "  docker exec pawpal-vault vault kv get secret/pawpal-finder/google"
    echo "  docker exec pawpal-vault vault kv get secret/pawpal-finder/database"
    echo ""
    echo "To run the application with Vault:"
    echo "  export VAULT_ROLE_ID=$VAULT_ROLE_ID"
    echo "  export VAULT_SECRET_ID=$VAULT_SECRET_ID"
    echo "  ./mvnw spring-boot:run -Dspring-boot.run.profiles=vault"
    echo ""
    echo "=========================================="
    
    # Create .env.example file
    cat > .env.example <<EOF
# Vault AppRole Credentials
# Copy this file to .env and fill in your actual credentials
# DO NOT commit .env to version control!

VAULT_ROLE_ID=your-role-id-here
VAULT_SECRET_ID=your-secret-id-here
VAULT_ADDR=http://localhost:8200
EOF
    print_success "Created .env.example file"
    
else
    print_error "Failed to find AppRole credentials file"
    exit 1
fi

echo ""
print_info "Next steps:"
echo "1. Update your Google API key in Vault:"
echo "   docker exec -e VAULT_TOKEN=dev-root-token pawpal-vault vault kv put secret/pawpal-finder/google api-key='YOUR_ACTUAL_GOOGLE_API_KEY'"
echo ""
echo "2. Start your Spring Boot application with the vault profile:"
echo "   export VAULT_ROLE_ID=$VAULT_ROLE_ID"
echo "   export VAULT_SECRET_ID=$VAULT_SECRET_ID"
echo "   ./mvnw spring-boot:run -Dspring-boot.run.profiles=vault"
echo ""
print_success "Setup script completed successfully!"
