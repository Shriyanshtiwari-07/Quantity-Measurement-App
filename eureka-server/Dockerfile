# ── Stage 1: build ────────────────────────────────────────────
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app

# Copy pom first — lets Docker cache the dependency layer.
# Re-downloads only when pom.xml changes, not on every code edit.
COPY pom.xml .
RUN mvn dependency:go-offline -q

COPY src ./src
RUN mvn clean package -DskipTests -q

# ── Stage 2: runtime ──────────────────────────────────────────
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

COPY --from=build /app/target/eureka-server-1.0.0-SNAPSHOT.jar app.jar

EXPOSE 8761

ENTRYPOINT ["java", "-Xmx200m", "-jar", "app.jar"]
