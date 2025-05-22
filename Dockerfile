# Use an OpenJDK base image
FROM eclipse-temurin:17-jdk-jammy

# Expose port
EXPOSE 8080

# Add a volume pointing to /tmp
VOLUME /tmp

# Copy the application JAR (replace with your actual JAR file name)
COPY target/myapp.jar app.jar

# Run the JAR file
ENTRYPOINT ["java","-jar","/app.jar"]
