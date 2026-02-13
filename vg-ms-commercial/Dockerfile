FROM public.ecr.aws/amazoncorretto/amazoncorretto:21-alpine-jdk AS build
WORKDIR /app
COPY . .
RUN chmod +x mvnw && ./mvnw clean package -DskipTests

FROM public.ecr.aws/amazoncorretto/amazoncorretto:21-alpine-jdk
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8094
ENTRYPOINT ["sh", "-c", "java -jar app.jar"]
