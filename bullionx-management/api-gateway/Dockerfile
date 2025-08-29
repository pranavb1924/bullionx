# Build stage
FROM maven:3.9-eclipse-temurin-21-alpine AS build
WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline -B
COPY src ./src
RUN mvn clean package -DskipTests

# Runtime stage
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
RUN addgroup -g 1000 spring && \
    adduser -D -u 1000 -G spring spring
COPY --from=build /app/target/*.jar app.jar
RUN chown -R spring:spring /app
USER spring
EXPOSE 4004
ENTRYPOINT ["java", "-jar", "app.jar"]