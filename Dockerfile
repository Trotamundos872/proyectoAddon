# preparar
FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

# ejecutar
FROM eclipse-temurin:21-jre
WORKDIR /app
ARG BACKEND_PORT
ENV BACKEND_PORT=${BACKEND_PORT}
COPY --from=build /app/target/*.jar app.jar
EXPOSE ${BACKEND_PORT}
ENTRYPOINT ["java", "-jar", "app.jar"]
