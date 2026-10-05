FROM maven:3.9-eclipse-temurin-17 AS build

WORKDIR /app

COPY pom.xml .

COPY src ./src

RUN mvn clean package -DskipTests


FROM tomcat:11.0.26-jdk17-temurin-jammy

RUN rm -rf /usr/local/tomcat/webapps/*

COPY --from=build /app/target/railway.war \
     /usr/local/tomcat/webapps/ROOT.war

COPY render-server.xml \
     /usr/local/tomcat/conf/server.xml

EXPOSE 10000

CMD ["catalina.sh", "run"]
