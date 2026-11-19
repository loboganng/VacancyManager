# Etapa 1: compila o projeto com Maven (imagem grande, só para o build)
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app
# Copiar o pom antes do código deixa o download das dependências em cache entre builds
COPY pom.xml .
RUN mvn -q dependency:go-offline
COPY src ./src
RUN mvn -q -DskipTests package

# Etapa 2: só o JRE e o .jar (imagem final bem menor)
FROM eclipse-temurin:17-jre
WORKDIR /app
RUN useradd --system --no-create-home app
COPY --from=build /app/target/*.jar app.jar
USER app
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
