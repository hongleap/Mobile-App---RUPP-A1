#!/bin/bash

# Stop existing containers
echo "Stopping existing containers..."
docker compose down

# Build and start services
echo "Building and starting services..."
docker compose up --build -d

echo "Services started!"
docker compose ps
