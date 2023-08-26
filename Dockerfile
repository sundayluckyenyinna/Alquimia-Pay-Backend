#
# Build stage
#
FROM maven:3.8.2-jdk-11 AS build
COPY . .
RUN mvn clean package -DskipTests

#
# Package stage
#
FROM openjdk:11-jdk-slim
COPY --from=build /target/alquimiapay-1.1.0.jar alquimiapay.jar
# ENV PORT=8080
EXPOSE 8080
ENTRYPOINT ["java","-jar","alquimiapay.jar"]