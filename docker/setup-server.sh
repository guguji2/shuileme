#!/bin/bash
# Server-side deployment script for Slema Backend
# Upload slema-backend.tar first, then run: bash setup-server.sh

set -e

echo "=== Slema Backend Deployment Script (Server) ==="

# Colors
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
NC='\033[0m' # No Color

# Config variables
MYSQL_HOST="${MYSQL_HOST:-127.0.0.1}"
MYSQL_PORT="${MYSQL_PORT:-3306}"
MYSQL_DB="${MYSQL_DB:-slema}"
MYSQL_USER="${MYSQL_USER:-root}"
BACKEND_PORT="${BACKEND_PORT:-8080}"

# Working directory
WORK_DIR="/home/www/slema"
ENV_FILE="$WORK_DIR/.env"
TAR_FILE="/root/slema-backend.tar"

echo -e "${YELLOW}Enter MySQL configuration:${NC}"
read -p "MySQL host (default: $MYSQL_HOST): " input
MYSQL_HOST=${input:-$MYSQL_HOST}

read -sp "MySQL password: " MYSQL_PASSWORD
echo
read -p "Confirm password: " MYSQL_PASSWORD_CONFIRM

if [ "$MYSQL_PASSWORD" != "$MYSQL_PASSWORD_CONFIRM" ]; then
    echo -e "${RED}Passwords do not match!${NC}"
    exit 1
fi

read -p "JWT Secret (press Enter to auto-generate): " JWT_SECRET
if [ -z "$JWT_SECRET" ]; then
    JWT_SECRET=$(openssl rand -base64 32)
    echo -e "${GREEN}JWT Secret generated${NC}"
fi

# Step 1: Check tar file
echo -e "\n${YELLOW}[1/5] Checking image file...${NC}"
if [ ! -f "$TAR_FILE" ]; then
    echo -e "${RED}Error: $TAR_FILE not found${NC}"
    echo "Please upload the image file first: scp slema-backend.tar root@server:/root/"
    exit 1
fi
echo -e "${GREEN}OK: Image file exists${NC}"

# Step 2: Load image
echo -e "\n${YELLOW}[2/5] Loading Docker image...${NC}"
docker load -i "$TAR_FILE"
echo -e "${GREEN}OK: Image loaded${NC}"

# Step 3: Create work directory and .env
echo -e "\n${YELLOW}[3/5] Creating environment configuration...${NC}"
mkdir -p "$WORK_DIR"

cat > "$ENV_FILE" << EOF
# Database configuration
SPRING_DATASOURCE_URL=jdbc:mysql://${MYSQL_HOST}:${MYSQL_PORT}/${MYSQL_DB}?useUnicode=true&characterEncoding=utf8&useSSL=false&serverTimezone=Asia/Shanghai&allowPublicKeyRetrieval=true
SPRING_DATASOURCE_USERNAME=${MYSQL_USER}
SPRING_DATASOURCE_PASSWORD=${MYSQL_PASSWORD}

# JWT configuration
JWT_SECRET=${JWT_SECRET}
JWT_EXPIRATION=604800000

# Email configuration (optional)
SPRING_MAIL_HOST=smtp.qq.com
SPRING_MAIL_PORT=587
SPRING_MAIL_USERNAME=153524502@qq.com
SPRING_MAIL_PASSWORD=luaqndiyethwbjie
EOF

chmod 600 "$ENV_FILE"
echo -e "${GREEN}OK: Environment file created: $ENV_FILE${NC}"

# Step 4: Create database (if not exists)
echo -e "\n${YELLOW}[4/5] Checking database...${NC}"
read -p "Create database '$MYSQL_DB'? (y/n): " create_db

if [ "$create_db" = "y" ]; then
    mysql -h "$MYSQL_HOST" -P "$MYSQL_PORT" -u "$MYSQL_USER" -p"$MYSQL_PASSWORD" << EOF
CREATE DATABASE IF NOT EXISTS \`${MYSQL_DB}\` CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
SHOW DATABASES;
EOF
    echo -e "${GREEN}OK: Database created${NC}"
fi

# Step 5: Start container
echo -e "\n${YELLOW}[5/5] Starting backend container...${NC}"

# Stop old container
docker stop slema-backend 2>/dev/null || true
docker rm slema-backend 2>/dev/null || true

# Start new container
docker run -d \
  --name slema-backend \
  --restart unless-stopped \
  --env-file "$ENV_FILE" \
  -p "${BACKEND_PORT}:8080" \
  slema-backend:latest

echo -e "${GREEN}OK: Container started${NC}"

# Wait for service to start
echo -e "\n${YELLOW}Waiting for service to start...${NC}"
sleep 10

# Step 6: Verify
echo -e "\n${YELLOW}[Verify] Testing service status...${NC}"

# Check container status
if docker ps | grep -q slema-backend; then
    echo -e "${GREEN}OK: Container is running${NC}"
else
    echo -e "${RED}ERROR: Container not running${NC}"
    docker logs slema-backend
    exit 1
fi

# Test health check
if curl -sf "http://localhost:${BACKEND_PORT}/api/health/" > /dev/null; then
    echo -e "${GREEN}OK: Service health check passed${NC}"
else
    echo -e "${RED}ERROR: Health check failed${NC}"
    echo "View logs: docker logs slema-backend"
    exit 1
fi

# Done
echo -e "\n${GREEN}=== Deployment Complete ===${NC}"
echo -e "Backend URL: ${YELLOW}http://$(hostname -I | awk '{print $1}'):${BACKEND_PORT}${NC}"
echo -e "Health check: ${YELLOW}curl http://localhost:${BACKEND_PORT}/api/health/${NC}"
echo -e "View logs: ${YELLOW}docker logs -f slema-backend${NC}"
echo -e "Stop service: ${YELLOW}docker stop slema-backend${NC}"
echo -e "Restart service: ${YELLOW}docker restart slema-backend${NC}"

# Firewall hint
echo -e "\n${YELLOW}If external access is needed, open firewall port ${BACKEND_PORT}:${NC}"
echo -e "firewalld: ${YELLOW}sudo firewall-cmd --permanent --add-port=${BACKEND_PORT}/tcp && sudo firewall-cmd --reload${NC}"
echo -e "ufw: ${YELLOW}sudo ufw allow ${BACKEND_PORT}/tcp${NC}"
