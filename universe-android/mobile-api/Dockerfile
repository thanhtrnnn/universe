# UniVerse Mobile API - build & run trên Render (Java 17, HTTP server thuần)
# Stage 1: build jar bằng Maven
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml .
RUN mvn -q -B dependency:go-offline
COPY src ./src
RUN mvn -q -B clean package -DskipTests

# Stage 2: runtime gọn nhẹ chỉ chứa JRE + jar
FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=build /app/target/universe-mobile-api.jar app.jar
# Render tự cấp biến PORT; app đã đọc PORT trong Config.java
CMD ["java", "-jar", "app.jar"]
