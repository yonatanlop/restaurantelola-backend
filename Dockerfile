# ---- Etapa de build ----
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app

# Cachear dependencias primero: solo se re-descargan si pom.xml cambia
COPY pom.xml .
RUN mvn -B -q dependency:go-offline

COPY src ./src
RUN mvn -B -q clean package -DskipTests

# ---- Etapa de runtime ----
# jre-alpine no publica build para arm64 (VM de despliegue), se usa jammy
FROM eclipse-temurin:17-jre-jammy
WORKDIR /app

RUN groupadd -r spring && useradd -r -g spring spring
COPY --from=build /app/target/*.jar app.jar
USER spring

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
