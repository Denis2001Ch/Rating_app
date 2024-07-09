FROM openjdk:11
ADD /target/sweater-1.0-SNAPSHOT.jar backend.jar
EXPOSE 8081
ENTRYPOINT ["java","-jar","backend.jar"]