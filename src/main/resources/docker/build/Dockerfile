FROM eclipse-temurin:21-jre-alpine

EXPOSE 8080/tcp
ENV JAVA_OPTS="-Xmx512m"

RUN mkdir /app
WORKDIR /app

COPY app.jar /app/app.jar
COPY entrypoint.sh /app/entrypoint.sh

RUN chmod +x /app/entrypoint.sh
CMD ["/app/entrypoint.sh"]