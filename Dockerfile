# Derleme aşaması
FROM openjdk:17 AS build
WORKDIR /app
COPY TodoApp.java .
RUN javac TodoApp.java

# Çalışma aşaması
FROM openjdk:17-jdk-slim
WORKDIR /app
COPY --from=build /app/TodoApp.class .
EXPOSE 8080
CMD ["java", "TodoApp"]
