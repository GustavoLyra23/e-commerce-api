FROM openjdk:21-jdk-slim
RUN groupadd --system appgroup && useradd --system --gid appgroup appuser
WORKDIR /project-ecommerce
RUN chown -R appuser:appgroup /project-ecommerce
COPY target/e-commerce-api-0.0.1-SNAPSHOT.jar.original .
USER appuser
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "e-commerce-api-0.0.1-SNAPSHOT.jar.original"]
