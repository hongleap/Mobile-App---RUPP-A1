#!/bin/bash

# Script to start microservices with Docker Compose

echo "🚀 Starting Microservices..."

# Check if docker is installed
if ! command -v docker &> /dev/null; then
    echo "❌ Docker is not installed. Please install Docker first."
    exit 1
fi

# Check if docker-compose.yml exists
if [ ! -f "docker-compose.yml" ]; then
    echo "❌ docker-compose.yml not found in current directory."
    echo "Please run this script from the services/ directory."
    exit 1
fi

# Check if firebase-service-account.json exists
if [ ! -f "firebase-service-account.json" ]; then
    echo "⚠️  WARNING: firebase-service-account.json not found!"
    echo "Please download it from Firebase Console and save it as:"
    echo "services/firebase-service-account.json"
    echo ""
    read -p "Continue anyway? (y/n) " -n 1 -r
    echo
    if [[ ! $REPLY =~ ^[Yy]$ ]]; then
        exit 1
    fi
fi

# Try docker compose (newer version) first, then docker-compose (older version)
echo "📦 Building and starting services..."
if docker compose version &> /dev/null; then
    echo "Using: docker compose (V2)"
    docker compose up --build
elif docker-compose version &> /dev/null; then
    echo "Using: docker-compose (V1)"
    docker-compose up --build
else
    echo "❌ Neither 'docker compose' nor 'docker-compose' found."
    echo "Please install Docker Compose."
    exit 1
fi

