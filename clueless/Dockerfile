# Use an official OpenJDK runtime as a parent image
FROM openjdk:21-jdk-slim

# Set the working directory in the container
WORKDIR /app

# Copy the built JAR file into the container
COPY target/clueless-1.0-SNAPSHOT.jar /app/clueless.jar

# Expose the server port
EXPOSE 3834


# Default to running as a server
ARG MODE=s

# Command to run the application
CMD ["java", "-jar", "/app/clueless.jar", "$MODE"]