FROM maven:3.9.16-eclipse-temurin-21 AS build

WORKDIR /app
COPY . .
RUN mvn package -DskipTests

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

EXPOSE 8080

COPY --from=build /app/target/bookt-0.0.1-SNAPSHOT.jar bookt.jar
CMD ["java", "-jar", "bookt.jar"]

