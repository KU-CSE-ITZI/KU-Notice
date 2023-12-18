FROM amazoncorretto:17
ARG JAR_FILE=build/libs/KU-Notice.jar
COPY ${JAR_FILE} app.jar
ENTRYPOINT ["java","-jar","/app.jar"]