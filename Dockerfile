# ========================================================
# Dockerfile for Multi Banking Transaction System
# Compatible with Render, Railway, Fly.io, Koyeb, AWS, etc.
# ========================================================

FROM eclipse-temurin:17-jdk-alpine AS build
WORKDIR /app

# Copy all project files
COPY . .

# Compile classes and copy resources
RUN mkdir -p WebContent/WEB-INF/classes && \
    javac -cp "WebContent/WEB-INF/lib/*" -d WebContent/WEB-INF/classes \
          src/com/banking/model/*.java \
          src/com/banking/dao/*.java \
          src/com/banking/servlet/*.java \
          runner/LocalServer.java && \
    cp src/db.properties WebContent/WEB-INF/classes/

# Runtime stage
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

COPY --from=build /app /app

ENV PORT=8080
EXPOSE 8080

CMD ["java", "-cp", "WebContent/WEB-INF/classes:WebContent/WEB-INF/lib/*", "runner.LocalServer"]
