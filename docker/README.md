# Slema Backend Deployment Guide (Method B)

Deploy backend only, using existing MySQL on server.

---

## Quick Start (3 Steps)

### Step 1: Build & Export (Local)

**Make sure Docker Desktop is running first**

```powershell
cd D:\app\slema\docker
.\export-image.ps1
```

This will:
- Build Docker image
- Export to tar file
- Compress the file

### Step 2: Upload to Server

```powershell
scp output\slema-backend.tar.gz root@YOUR_SERVER_IP:/root/
```

### Step 3: Deploy on Server

SSH to your server and run:

```bash
# Extract
tar -xzf slema-backend.tar.gz

# Run deployment script
bash setup-server.sh
```

The script will guide you through:
- Loading Docker image
- Entering MySQL configuration
- Creating database
- Starting backend container
- Verifying service status

---

## Files in docker/

| File | Description |
|------|-------------|
| `Dockerfile` | Backend image build file |
| `export-image.ps1` | Local export script |
| `setup-server.sh` | Server deployment script |
| `README.md` | This file |

---

## Manual Deployment (Without Script)

If you prefer manual setup:

### 1. Build & Load Image

```bash
# Local
docker build -f docker/Dockerfile -t slema-backend:latest .
docker save slema-backend:latest -o slema-backend.tar

# Upload to server
scp slema-backend.tar root@server:/root/

# On server
docker load -i /root/slema-backend.tar
```

### 2. Create Database

```bash
mysql -u root -p
```

```sql
CREATE DATABASE IF NOT EXISTS slema CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
exit;
```

### 3. Create Environment File

```bash
mkdir -p /home/www/slema
cat > /home/www/slema/.env << 'EOF'
SPRING_DATASOURCE_URL=jdbc:mysql://SERVER_IP:3306/slema?useUnicode=true&characterEncoding=utf8&useSSL=false&serverTimezone=Asia/Shanghai&allowPublicKeyRetrieval=true
SPRING_DATASOURCE_USERNAME=root
SPRING_DATASOURCE_PASSWORD=YOUR_MYSQL_PASSWORD
JWT_SECRET=CHANGE_THIS_TO_A_RANDOM_256_BIT_STRING
EOF

chmod 600 /home/www/slema/.env
```

### 4. Start Container

```bash
docker run -d \
  --name slema-backend \
  --restart unless-stopped \
  --env-file /home/www/slema/.env \
  -p 8080:8080 \
  slema-backend:latest
```

### 5. Verify

```bash
# Check container
docker ps | grep slema-backend

# View logs
docker logs slema-backend

# Test health check
curl http://localhost:8080/api/health/
```

---

## Update Deployment

```bash
# Stop and remove old container
docker stop slema-backend
docker rm slema-backend

# Load new image
docker load -i /root/slema-backend-new.tar

# Start new container (same .env file)
docker run -d \
  --name slema-backend \
  --restart unless-stopped \
  --env-file /home/www/slema/.env \
  -p 8080:8080 \
  slema-backend:latest
```

---

## Common Issues

### Cannot connect to MySQL

**Error**: `Communications link failure`

**Solutions**:
1. Check MySQL allows remote connections: `bind-address = 0.0.0.0` in `/etc/my.cnf`
2. Check firewall allows port 3306
3. Check MySQL user permissions: `GRANT ALL ON slema.* TO 'user'@'%';`

### Container exits immediately

**Check logs**:
```bash
docker logs slema-backend
```

### Health check fails

```bash
# Test manually
curl http://localhost:8080/api/health/

# View detailed logs
docker logs slema-backend --tail 50
```

---

## Firewall Configuration

If you need external access to the backend API:

```bash
# firewalld
sudo firewall-cmd --permanent --add-port=8080/tcp
sudo firewall-cmd --reload

# ufw
sudo ufw allow 8080/tcp

# Don't forget to allow port in cloud server security group
```

**Recommendation**: Use Nginx reverse proxy with HTTPS in production.

---

## MySQL Remote Access Setup

If MySQL is on the same server as backend, use `127.0.0.1` or `host.docker.internal`.

If on different servers:

```bash
# Edit MySQL config
sudo vim /etc/my.cnf

# Add or modify:
[mysqld]
bind-address = 0.0.0.0

# Restart MySQL
sudo systemctl restart mysql

# Grant remote access
mysql -u root -p
```

```sql
GRANT ALL PRIVILEGES ON slema.* TO 'root'@'%' IDENTIFIED BY 'password';
FLUSH PRIVILEGES;
```
