# ---- Build stage ----
FROM maven:3.9.9-eclipse-temurin-21 AS build
WORKDIR /app

# 1) Cache deps
COPY pom.xml .
RUN mvn -q -B -DskipTests dependency:go-offline

# 2) Build
COPY src ./src
RUN mvn -q -B -DskipTests package

# 3) Normalize the jar name (exclude the "original-" jar)
RUN JAR="$(ls target/*.jar | grep -v 'original-' | head -n 1)" && \
    mv "$JAR" target/app.jar

# ---- Runtime stage ----
FROM eclipse-temurin:21-jre AS runner
WORKDIR /app

# Copy the normalized jar
COPY --from=build /app/target/app.jar /app/app.jar

EXPOSE 8080
ENTRYPOINT ["java","-jar","/app/app.jar"]
