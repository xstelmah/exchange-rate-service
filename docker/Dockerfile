# Use a multi-stage build to keep the final image small

# Stage 1: Build the application
FROM maven:3.9-eclipse-temurin-21 as build

# Set the working directory in the container
WORKDIR /app

# Copy the pom.xml and download dependencies
COPY pom.xml .

# Copy the source code and build the application
COPY src ./src
RUN mvn clean package -DskipTests

# Stage 2: Run the application
FROM eclipse-temurin:21-jre-alpine

ENV JAVA_OPTS="-Xmx512m"
# Set the working directory in the container
WORKDIR /app

# Copy the JAR file from the build stage
COPY --from=build /app/target/*.jar app.jar

COPY ./docker/entrypoint.sh /app/entrypoint.sh
RUN chmod +x /app/entrypoint.sh

# Expose the port that your application will run on
EXPOSE 8080

# Run the application
CMD ["/app/entrypoint.sh"]