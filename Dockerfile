# Сборка фронтенда и размещение в бэкенде
# cd frontend
# npm run build
# cp -r dist/* ../src/main/resources/static/
# mkdir -p ../src/main/resources/static/pages
# mkdir -p ../src/main/resources/static/src
# mv ../src/main/resources/static/index.html ../src/main/resources/static/pages/
# cp -r src/assets ../src/main/resources/static/src/

# сборка бэкенда
# mvn clean package -DskipTests
# docker build -t rhinorus/mylink:latest .
# docker push rhinorus/mylink:latest

FROM eclipse-temurin:17-jre-jammy

WORKDIR /app
COPY target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]