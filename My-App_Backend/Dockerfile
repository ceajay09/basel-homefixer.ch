FROM amazoncorretto:17-al2023
RUN dnf install -y curl-minimal && dnf clean all
WORKDIR /app
# Only the validated release copy, never target/my-app*.jar.
COPY target/release/app.jar /app/app.jar
ENV SPRING_CONFIG_LOCATION=file:/etc/basel-homefixer/application-production.properties
USER 10001:10001
EXPOSE 8081
ENTRYPOINT ["java", "-Djava.awt.headless=true", "-jar", "/app/app.jar"]
