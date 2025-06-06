# Base image with JDK 17 or 21
FROM openjdk:21-jdk-slim

# Set working directory
WORKDIR /app

# Copy jar file
COPY target/*.jar app.jar

# Expose port (adjust if needed)
EXPOSE 8080

# Start the app
ENTRYPOINT ["java", "-jar", "app.jar"]
