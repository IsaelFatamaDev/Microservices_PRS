FROM public.ecr.aws/amazoncorretto/amazoncorretto:21-alpine-jdk AS build
WORKDIR /app
COPY pom.xml mvnw ./
COPY .mvn .mvn
COPY src ./src
RUN chmod +x mvnw && ./mvnw package -DskipTests -q

FROM public.ecr.aws/amazoncorretto/amazoncorretto:21-alpine-jdk
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8083
ENTRYPOINT ["sh", "-c", "java -jar app.jar"]
