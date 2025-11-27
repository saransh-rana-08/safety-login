# ---------- Build stage ----------
FROM maven:3.9.5-eclipse-temurin-17 AS build

# Set working dir
WORKDIR /app

# Copy pom first to leverage Docker cache for dependencies
COPY pom.xml .
# If you use .mvn wrapper, copy it too
COPY .mvn .mvn
COPY mvnw mvnw

# Download dependencies (optional but speeds up iterative builds)
RUN mvn -B -f pom.xml -nsu dependency:go-offline

# Copy source and build
COPY src ./src
RUN mvn -B -DskipTests package

# ---------- Run stage ----------
FROM eclipse-temurin:17-jre

# Create app user (optional, improves security)
RUN addgroup --system appgroup && adduser --system appuser && mkdir /app
WORKDIR /app

# Copy jar from the build stage (assumes single jar in target)
COPY --from=build /app/target/*.jar app.jar

# Expose port used by Spring Boot
EXPOSE 8080

# Recommended JVM flags (tune if you need)
ENV JAVA_OPTS="-Xms256m -Xmx512m -Djava.security.egd=file:/dev/./urandom"

HEALTHCHECK --interval=30s --timeout=5s --start-period=10s --retries=3 \
  CMD curl -f http://localhost:8080/actuator/health || exit 1

# Run the jar
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar /app/app.jar"]
