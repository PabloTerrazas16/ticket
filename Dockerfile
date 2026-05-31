FROM maven:3.9.8-eclipse-temurin-17 AS builder

WORKDIR /build

COPY pom.xml .
COPY mvnw .
COPY .mvn .mvn

RUN mvn dependency:resolve

COPY src src

RUN mvn clean package -DskipTests

FROM eclipse-temurin:24.0.2_12-jre-alpine

WORKDIR /app

RUN addgroup -S ticket && adduser -S ticket -G ticket

COPY --from=builder /build/target/*.jar app.jar

RUN chown -R ticket:ticket /app

USER ticket

HEALTHCHECK --interval=30s --timeout=5s --start-period=5s --retries=3 \
    CMD java -cp app.jar org.springframework.boot.loader.PropertiesLauncher \
    -Dspring.profiles.active=health || exit 1

EXPOSE 8080

ENV SPRING_PROFILES_ACTIVE=prod \
    JAVA_OPTS="-Xms256m -Xmx512m -XX:+UseG1GC -XX:MaxGCPauseMillis=200"

ENTRYPOINT ["java", "-jar", "app.jar"]
