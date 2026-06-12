FROM eclipse-temurin:25-jdk AS build
WORKDIR /workspace
COPY pom.xml .
COPY src ./src
RUN apt-get update \
    && apt-get install -y --no-install-recommends maven \
    && mvn -ntp -DskipTests package \
    && mv target/*.jar /workspace/app.jar \
    && apt-get purge -y maven \
    && rm -rf /var/lib/apt/lists/*

FROM eclipse-temurin:25-jre
WORKDIR /app
COPY --from=build /workspace/app.jar ./app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
