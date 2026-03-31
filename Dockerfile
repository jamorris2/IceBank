# ---- Build ----
FROM gradle:8.5-jdk21 AS build

WORKDIR /app

COPY . .

RUN gradle build -x test

# ---- Run ----
FROM eclipse-temurin:21-jdk-jammy

WORKDIR /app

COPY --from=build /app/build/libs/*.jar app.jar

ENV PORT=8082
EXPOSE 8082

ENTRYPOINT ["java", "-jar", "app.jar"]