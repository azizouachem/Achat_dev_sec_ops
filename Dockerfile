FROM eclipse-temurin:17-jre-alpine

# OWASP A05 – Security Misconfiguration: remove unnecessary packages
RUN apk --no-cache upgrade && \
    rm -rf /var/cache/apk/*

# OWASP A05 – Non-root user (Principle of Least Privilege)
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring

VOLUME /tmp
EXPOSE 8089

# OWASP A08 – Software Integrity: use COPY instead of ADD to avoid auto-extraction risks
COPY target/achat-1.0.jar app.jar

# Health check for container orchestration (A09 – Monitoring)
HEALTHCHECK --interval=30s --timeout=5s --retries=3 \
    CMD wget --no-verbose --tries=1 --spider http://localhost:8089/SpringMVC/actuator/health || exit 1

ENTRYPOINT ["java", "-Djava.security.egd=file:/dev/./urandom", "-jar", "/app.jar"]

