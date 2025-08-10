# --- Stage 1: build ---
FROM maven:3.9.9-eclipse-temurin-21 AS build
WORKDIR /build
COPY pom.xml .
COPY src ./src
RUN mvn -DskipTests clean package

# --- Stage 2: runtime ---
FROM eclipse-temurin:21-jre
WORKDIR /app
# copia o JAR reempacotado do Spring Boot (não o .original)
COPY --from=build /build/target/*-SNAPSHOT.jar /app/app.jar

# Render passa a porta via $PORT
ENV PORT=8081
EXPOSE 8081
ENTRYPOINT ["sh","-c","java -jar /app/app.jar --server.port=${PORT}"]
