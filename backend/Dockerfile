# Stage 1: Build the application using a Maven image that includes Java 21
FROM maven:3.9.6-eclipse-temurin-21 AS build

# Set the working directory inside the container
WORKDIR /app

# Copy the entire project into the container
COPY . .

# Run the Maven command to build the project's JAR file, skipping tests
RUN mvn clean package -DskipTests

# Stage 2: Create the final, smaller production image using just the JRE
FROM eclipse-temurin:21-jre

# Set the working directory for the final image
WORKDIR /app

# Copy only the compiled JAR file from the 'build' stage
COPY --from=build /app/target/*.jar app.jar

# Tell Docker that the container listens on port 8080
EXPOSE 8080

# The command to run when the container starts
ENTRYPOINT ["java", "-jar", "app.jar"]