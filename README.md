# 🎮 GamerMajilis - Gaming Community Platform

**GamerMajilis** is a comprehensive gaming community platform built with Spring Boot and PostgreSQL. It provides tournament management, user authentication, Discord integration, and social features for gamers.

## 🌟 Features

- 🏆 **Tournament Management** - Create, manage, and participate in tournaments
- 👥 **User Authentication** - Email/password and Discord OAuth login
- 🔐 **JWT Security** - Secure API endpoints with role-based access
- 📧 **Email Integration** - User verification and notifications
- 🎯 **RESTful APIs** - Complete CRUD operations for tournaments and users
- 🐳 **Docker Ready** - One-command deployment with Docker Compose
- 📚 **API Documentation** - Interactive Swagger UI

## 🚀 Quick Start with Docker

### Prerequisites

- **Docker** - [Install Docker](https://docs.docker.com/get-docker/)
- **Docker Compose** - Usually included with Docker Desktop
- **Git** - For cloning the repository

### 1. Clone & Setup

```bash
# Clone the repository
git clone https://github.com/yourusername/GamerMajilis.git
cd GamerMajilis

# Create environment file from template
cp docker-env-template .env

# (Optional) Edit .env file with your credentials
nano .env
```

### 2. Run the Application

```bash
# Option A: Use the automated setup script
chmod +x docker-setup.sh
./docker-setup.sh

# Option B: Manual Docker Compose
docker-compose up --build -d
```

### 3. Access the Application

- **🌐 Main API:** http://localhost:8080/api
- **📚 Swagger UI:** http://localhost:8080/api/swagger-ui.html
- **💾 Database:** localhost:5433 (PostgreSQL)

## 📋 Environment Configuration

### Required Environment Variables

Edit the `.env` file to configure:

```bash
# Database (Docker internal - usually no changes needed)
DATABASE_URL=jdbc:postgresql://postgres:5432/gamermajilis_db
DATABASE_USERNAME=gamermajilis_user
DATABASE_PASSWORD=gamermajilis_pass123

# Email Configuration (for user verification)
EMAIL_USERNAME=your-email@gmail.com
EMAIL_PASSWORD=your-app-specific-password

# Discord OAuth (for Discord login)
DISCORD_CLIENT_ID=your-discord-client-id
DISCORD_CLIENT_SECRET=your-discord-client-secret

# Security
JWT_SECRET=your-super-secret-jwt-key
```

### Getting Credentials

#### Gmail App Password
1. Enable 2FA on your Gmail account
2. Go to Google Account → Security → App passwords
3. Generate an app-specific password
4. Use this password (not your regular Gmail password)

#### Discord OAuth Setup
1. Go to [Discord Developer Portal](https://discord.com/developers/applications)
2. Create a new application
3. Go to "OAuth2" section
4. Copy Client ID and Client Secret
5. Add redirect URI: `http://localhost:8080/api/login/oauth2/code/discord`

## 🔧 Docker Management

### Basic Commands

```bash
# Start the application
docker-compose up -d

# View logs
docker-compose logs -f

# View specific service logs
docker-compose logs -f app
docker-compose logs -f postgres

# Stop the application
docker-compose down

# Rebuild after code changes
docker-compose up --build -d

# View container status
docker-compose ps
```

### Database Management

```bash
# Access database
docker-compose exec postgres psql -U gamermajilis_user -d gamermajilis_db

# Backup database
docker-compose exec postgres pg_dump -U gamermajilis_user gamermajilis_db > backup.sql

# Reset database (⚠️ deletes all data)
docker-compose down -v
docker-compose up -d
```

## 🛠️ Development

### Local Development (without Docker)

If you prefer to run locally for development:

```bash
# Install Java 17 and Maven
# Install PostgreSQL and create database

# Update application.properties for local database
# Run the application
mvn spring-boot:run
```

### Making Code Changes

1. Make your code changes
2. Rebuild and restart: `docker-compose up --build -d`
3. Check logs: `docker-compose logs -f app`

## 📚 API Documentation

### Authentication Endpoints

| Method | Endpoint | Description | Auth Required |
|--------|----------|-------------|---------------|
| POST | `/auth/signup` | Register new user | No |
| POST | `/auth/login` | User login | No |
| GET | `/auth/verify-email` | Verify email address | No |
| POST | `/auth/resend-verification` | Resend verification email | No |
| GET | `/auth/me` | Get current user info | Yes |
| POST | `/auth/logout` | User logout | Yes |
| GET | `/auth/validate-token` | Validate JWT token | No |

### Tournament Endpoints

| Method | Endpoint | Description | Auth Required |
|--------|----------|-------------|---------------|
| GET | `/tournaments` | List all tournaments | Yes |
| POST | `/tournaments` | Create tournament | Yes |
| GET | `/tournaments/{id}` | Get tournament details | Yes |
| PUT | `/tournaments/{id}` | Update tournament | Yes |
| DELETE | `/tournaments/{id}` | Delete tournament | Yes |
| GET | `/tournaments/organizer/{id}` | Get tournaments by organizer | Yes |
| POST | `/tournaments/{id}/moderators` | Add moderator | Yes |
| POST | `/tournaments/{id}/view` | Increment view count | Yes |

### Tournament Participation Endpoints

| Method | Endpoint | Description | Auth Required |
|--------|----------|-------------|---------------|
| GET | `/tournaments/{id}/participants` | List participants | Yes |
| POST | `/tournaments/{id}/participants/register` | Register for tournament | Yes |
| POST | `/tournaments/{id}/participants/check-in` | Check-in participant | Yes |
| POST | `/tournaments/{id}/participants/submit-result` | Submit match result | Yes |
| POST | `/tournaments/{id}/participants/disqualify` | Disqualify participant | Yes |

### Example API Usage

#### Register a User
```bash
curl -X POST "http://localhost:8080/api/auth/signup" \
     -H "Content-Type: application/x-www-form-urlencoded" \
     -d "email=gamer@example.com&password=securepass123&displayName=ProGamer"
```

#### Login
```bash
curl -X POST "http://localhost:8080/api/auth/login" \
     -H "Content-Type: application/x-www-form-urlencoded" \
     -d "identifier=gamer@example.com&password=securepass123"
```

#### Create Tournament (with JWT token)
```bash
curl -X POST "http://localhost:8080/api/tournaments" \
     -H "Authorization: Bearer YOUR_JWT_TOKEN" \
     -H "Content-Type: application/json" \
     -d '{
       "name": "CS:GO Championship 2024",
       "description": "Annual tournament",
       "gameTitle": "Counter-Strike 2",
       "maxParticipants": 64,
       "startDate": "2024-12-01T10:00:00",
       "tournamentType": "SINGLE_ELIMINATION"
     }'
```

## 🏗️ Project Structure

```
GamerMajilis/
├── 🐳 Docker Configuration
│   ├── Dockerfile                 # Application container
│   ├── docker-compose.yml         # Multi-container setup
│   ├── .dockerignore              # Build exclusions
│   └── docker-setup.sh            # Automated setup
├── ☕ Spring Boot Application
│   ├── src/main/java/com/gamermajilis/
│   │   ├── controller/             # REST Controllers
│   │   ├── service/                # Business Logic
│   │   ├── model/                  # JPA Entities
│   │   ├── repository/             # Data Access
│   │   ├── security/               # JWT & OAuth2
│   │   └── config/                 # Configuration
│   └── src/main/resources/
│       └── application.properties  # App Configuration
├── 📁 Configuration
│   ├── docker-env-template         # Environment variables
│   └── pom.xml                     # Maven dependencies
└── 📚 Documentation
    ├── README.md                   # This file
    └── DOCKER-README.md            # Detailed Docker guide
```

## 🐛 Troubleshooting

### Common Issues

#### Port Already in Use
```bash
# Check what's using the port
lsof -i :8080
lsof -i :5433

# Or change ports in docker-compose.yml
```

#### Permission Denied (Docker)
```bash
# Add user to docker group (Linux)
sudo usermod -aG docker $USER
# Then logout/login

# Or use sudo temporarily
sudo docker-compose up -d
```

#### Application Won't Start
```bash
# Check logs for errors
docker-compose logs app

# Check database connection
docker-compose logs postgres

# Restart everything
docker-compose down && docker-compose up -d
```

#### Database Connection Issues
```bash
# Verify database is running
docker-compose ps postgres

# Test database connection
docker-compose exec postgres pg_isready -U gamermajilis_user
```

### Clean Restart
```bash
# Remove all containers and data
docker-compose down -v --rmi all

# Rebuild everything
docker-compose up --build -d
```

## 🔒 Security Notes

### For Production Deployment

1. ✅ Change all default passwords in `.env`
2. ✅ Use strong, unique JWT secret
3. ✅ Configure proper reverse proxy (nginx/Apache)
4. ✅ Enable HTTPS/SSL
5. ✅ Set appropriate firewall rules
6. ✅ Use Docker secrets for sensitive data
7. ✅ Regular security updates

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch: `git checkout -b feature/amazing-feature`
3. Make your changes
4. Test with Docker: `docker-compose up --build -d`
5. Commit your changes: `git commit -m 'Add amazing feature'`
6. Push to the branch: `git push origin feature/amazing-feature`
7. Open a Pull Request

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 👏 Acknowledgments

- Spring Boot community for the excellent framework
- Docker for containerization technology
- PostgreSQL for the robust database
- All contributors and testers

---

**Happy Gaming! 🎮**

For detailed Docker information, see [DOCKER-README.md](DOCKER-README.md)
