# 🐳 GamerMajilis Docker Guide

This guide explains how to run the **GamerMajilis** application using Docker containers. With Docker, you don't need to install Java, PostgreSQL, or configure any database settings manually.

## 📋 Prerequisites

1. **Install Docker**: [Download Docker](https://docs.docker.com/get-docker/)
2. **Install Docker Compose**: Usually included with Docker Desktop
3. **Git** (to clone the repository)

## 🚀 Quick Start

### Option 1: Automatic Setup (Recommended)

```bash
# Clone the repository
git clone <your-repo-url>
cd GamerMajilis

# Run the setup script
./docker-setup.sh
```

### Option 2: Manual Setup

```bash
# 1. Create environment file from template
cp docker-env-template .env

# 2. Start the application
docker-compose up --build -d

# 3. View logs (optional)
docker-compose logs -f
```

## ⚙️ Configuration

### Environment Variables

Edit the `.env` file to customize your application:

```bash
# Email Configuration (for user verification emails)
EMAIL_USERNAME=your-email@gmail.com
EMAIL_PASSWORD=your-app-specific-password

# Discord OAuth (for Discord login feature)
DISCORD_CLIENT_ID=your-discord-app-client-id
DISCORD_CLIENT_SECRET=your-discord-app-secret

# JWT Secret (change in production)
JWT_SECRET=your-super-secret-jwt-key
```

### Getting Discord OAuth Credentials

1. Go to [Discord Developer Portal](https://discord.com/developers/applications)
2. Create a new application
3. Go to "OAuth2" section
4. Copy Client ID and Client Secret
5. Add redirect URI: `http://localhost:8080/api/login/oauth2/code/discord`

### Getting Gmail App Password

1. Enable 2-Factor Authentication on your Gmail account
2. Go to Google Account Settings → Security → App passwords
3. Generate an app-specific password
4. Use this password (not your regular Gmail password)

## 🏃‍♂️ Running the Application

### Start the Application
```bash
docker-compose up -d
```

### Stop the Application
```bash
docker-compose down
```

### Restart After Configuration Changes
```bash
docker-compose restart
```

### Rebuild After Code Changes
```bash
docker-compose up --build -d
```

## 📊 Monitoring

### View Application Logs
```bash
# View all logs
docker-compose logs -f

# View only app logs
docker-compose logs -f app

# View only database logs
docker-compose logs -f postgres
```

### Check Container Status
```bash
docker-compose ps
```

### Access Container Shell
```bash
# Access app container
docker-compose exec app bash

# Access database container
docker-compose exec postgres psql -U gamermajilis_user -d gamermajilis_db
```

## 🌐 Access Points

Once running, you can access:

- **Main API**: http://localhost:8080/api
- **Swagger UI**: http://localhost:8080/api/swagger-ui.html
- **API Documentation**: http://localhost:8080/api/api-docs
- **Database**: localhost:5432 (if you need external access)

## 📁 Docker Architecture

```
GamerMajilis/
├── Dockerfile              # App container definition
├── docker-compose.yml      # Multi-container orchestration
├── .dockerignore           # Files to exclude from build
├── docker-env-template     # Environment variables template
├── docker-setup.sh         # Automated setup script
└── DOCKER-README.md        # This file
```

## 🔧 Useful Docker Commands

### Container Management
```bash
# Stop all containers
docker-compose down

# Stop and remove volumes (⚠️ deletes database data)
docker-compose down -v

# View resource usage
docker-compose top

# Scale services (if needed)
docker-compose up --scale app=2 -d
```

### Database Management
```bash
# Backup database
docker-compose exec postgres pg_dump -U gamermajilis_user gamermajilis_db > backup.sql

# Restore database
docker-compose exec -T postgres psql -U gamermajilis_user gamermajilis_db < backup.sql

# Reset database (⚠️ deletes all data)
docker-compose down -v postgres
docker-compose up postgres -d
```

### Development Commands
```bash
# Build without cache
docker-compose build --no-cache

# View environment variables
docker-compose config

# Update images
docker-compose pull
```

## 🐛 Troubleshooting

### Application Won't Start
```bash
# Check logs for errors
docker-compose logs app

# Check if database is ready
docker-compose exec postgres pg_isready -U gamermajilis_user
```

### Database Connection Issues
```bash
# Verify database container is running
docker-compose ps postgres

# Check database logs
docker-compose logs postgres

# Test database connection
docker-compose exec postgres psql -U gamermajilis_user -d gamermajilis_db -c "SELECT 1;"
```

### Port Already in Use
```bash
# Find what's using port 8080
lsof -i :8080

# Or change port in docker-compose.yml
# Change "8080:8080" to "8081:8080"
```

### Clean Start
```bash
# Remove all containers, networks, and volumes
docker-compose down -v --rmi all

# Rebuild everything
docker-compose up --build -d
```

## 🔒 Security Notes

**For Production Deployment:**

1. Change all default passwords in `.env`
2. Use strong JWT secret
3. Configure proper reverse proxy (nginx/Apache)
4. Enable HTTPS
5. Set appropriate firewall rules
6. Use Docker secrets for sensitive data

## 📦 What's Included

The Docker setup includes:
- ✅ **Spring Boot Application** (Java 17)
- ✅ **PostgreSQL Database** (Version 15)
- ✅ **Automatic Database Schema Creation**
- ✅ **Health Checks** for both services
- ✅ **Data Persistence** (database survives container restarts)
- ✅ **Network Isolation** for security
- ✅ **Log Management**
- ✅ **Development & Production Ready**

## 🆘 Need Help?

1. Check the logs: `docker-compose logs -f`
2. Verify Docker is running: `docker --version`
3. Check container status: `docker-compose ps`
4. Restart everything: `docker-compose restart`

---

**Happy Gaming! 🎮** 