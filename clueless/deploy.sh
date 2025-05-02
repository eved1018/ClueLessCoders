#!/bin/bash

# Build the Maven project
echo "Building the project..."
JAVA_HOME="/opt/homebrew/Cellar/openjdk@21/21.0.6/libexec/openjdk.jdk/Contents/Home" mvn --no-transfer-progress clean package

# Build the Docker image
echo "Building the Docker image..."
docker build -t clueless-server .

# Run the Docker container
echo "Running the Docker container..."
docker run -d -p 3834:3834 --name clueless-server clueless-server server

echo "Deployment complete. The server is running on port 3834."

# Run Client
docker run -d --name clueless-client clueless-server client