# backend/Dockerfile (Corrected)

# Stage 1: Build the application
FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app

# Corrected Path: Copy pom.xml from the current directory
COPY pom.xml .
RUN mvn dependency:go-offline

# Corrected Path: Copy src from the current directory
COPY src ./src
RUN mvn clean package -DskipTests

# Stage 2: Create the final production image
FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]