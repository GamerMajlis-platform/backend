#!/bin/bash

echo "🐳 GamerMajilis Docker Setup Script"
echo "=================================="

# Check if Docker is installed
if ! command -v docker &> /dev/null; then
    echo "❌ Docker is not installed. Please install Docker first:"
    echo "   https://docs.docker.com/get-docker/"
    exit 1
fi

# Check if Docker Compose is installed (try both old and new syntax)
if command -v docker-compose &> /dev/null; then
    DOCKER_COMPOSE="docker-compose"
elif docker compose version &> /dev/null; then
    DOCKER_COMPOSE="docker compose"
else
    echo "❌ Docker Compose is not installed. Please install Docker Compose first:"
    echo "   https://docs.docker.com/compose/install/"
    echo ""
    echo "💡 Alternative: Use newer Docker with built-in compose:"
    echo "   Try: docker compose --version"
    exit 1
fi

echo "✅ Docker is installed"
echo "✅ Docker Compose is installed"

# Create .env file from template if it doesn't exist
if [ ! -f .env ]; then
    echo "📝 Creating .env file from template..."
    cp docker-env-template .env
    echo "⚠️  Please update .env file with your actual credentials before running the application"
else
    echo "✅ .env file already exists"
fi

# Create necessary directories
echo "📁 Creating necessary directories..."
mkdir -p docker/init-db
mkdir -p logs

# Stop existing containers if running
echo "🛑 Stopping existing containers..."
$DOCKER_COMPOSE down

# Build and start the application
echo "🚀 Building and starting GamerMajilis application..."
$DOCKER_COMPOSE up --build -d

echo ""
echo "🎉 Setup complete!"
echo ""
echo "📋 Next steps:"
echo "1. Update the .env file with your actual credentials"
echo "2. Restart the application: docker-compose restart"
echo "3. Visit: http://localhost:8080/api/swagger-ui.html"
echo ""
echo "📊 Useful commands:"
echo "  View logs:    $DOCKER_COMPOSE logs -f"
echo "  Stop app:     $DOCKER_COMPOSE down"  
echo "  Start app:    $DOCKER_COMPOSE up -d"
echo "  Rebuild:      $DOCKER_COMPOSE up --build -d" 