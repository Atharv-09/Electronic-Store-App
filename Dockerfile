#use the official maven image to uild spring boot app
FROM maven:3.8.4-openjdk-17 AS uild

#setting working dir
WORKDIR /app

#copy pom.xml and install dependencies
COPY pom.xml .
RUN mvn dependency:go-offline

#copy src code and uild application
COPY src ./src
RUN mvn clean package -DskipTests

#use an official OpenJDK image to run the application
FROM openjdk:17-jdk-slim

#setting the workign directory
WORKDIR /app

#copy the built jar file from build stage
COPY --from=build /app/target/ecom.proj-0.0.1-SNAPSHOT.jar .

#expose to port
EXPOSE 8080

#commands
ENTRYPOINT ["java", "-jar", "/app/ecom.proj-0.0.1-SNAPSHOT.jar"]