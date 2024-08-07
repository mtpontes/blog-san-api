# Build jar
FROM openjdk:17-jdk-alpine AS BUILDER
WORKDIR /app

COPY pom.xml .
COPY mvnw .
COPY .mvn .mvn
COPY src src

RUN ./mvnw package -DskipTests

# Build docker image
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

COPY --from=BUILDER /app/target/*.jar app.jar

ENTRYPOINT [ "java", "-jar", "app.jar" ]