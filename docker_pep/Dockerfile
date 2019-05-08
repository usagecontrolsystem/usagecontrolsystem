FROM ubuntu:latest

RUN apt update -y

RUN DEBIAN_FRONTEND=noninteractive apt-get install -y --no-install-recommends apt-utils

RUN DEBIAN_FRONTEND=noninteractive apt-get install -y --no-install-recommends software-properties-common bash wget vim curl

RUN DEBIAN_FRONTEND=noninteractive apt upgrade -y

#RUN add-apt-repository ppa:webupd8team/java -y && \
#    apt update && \
#    echo oracle-java8-installer shared/accepted-oracle-license-v1-1 select true | /usr/bin/debconf-set-selections && \
#    apt install -y oracle-java8-installer
RUN DEBIAN_FRONTEND=noninteractive apt-get install -y openjdk-8-jdk

#RUN DEBIAN_FRONTEND=noninteractive apt-get install -y tomcat8

#RUN apt install -y mysql-server

RUN apt clean -y && rm -rf /var/lib/apt/lists/*

EXPOSE 9999
#EXPOSE 9998

#COPY ../UsageControlFramework/target/*-SNAPSHOT.jar ucs.jar
COPY pep.jar pep.jar
COPY policies policies
COPY requests requests
COPY application.properties application.properties

#RUN java -jar ucs.jar &
#RUN java -jar pep.jar &
ENTRYPOINT ["java", "-jar", "pep.jar", "-Dspring.config.location=./application.properties"]
