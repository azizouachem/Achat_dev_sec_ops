FROM eclipse-temurin:17-jre-alpine
VOLUME /tmp
EXPOSE 8089
ADD target/achat-1.0.jar app.jar
ENTRYPOINT ["java","-jar","/app.jar"]
