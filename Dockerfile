FROM eclipse-temurin:17-jre-alpine
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring
VOLUME /tmp
EXPOSE 8089
ADD target/achat-1.0.jar app.jar
ENTRYPOINT ["java","-jar","/app.jar"]
