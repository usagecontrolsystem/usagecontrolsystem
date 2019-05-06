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

RUN apt install -y mysql-server

RUN apt clean -y && rm -rf /var/lib/apt/lists/*

RUN mkdir /usr/local/tomcat
RUN wget https://www-eu.apache.org/dist/tomcat/tomcat-8/v8.5.40/bin/apache-tomcat-8.5.40.tar.gz -O /tmp/tomcat.tar.gz
RUN cd /tmp && tar xvfz tomcat.tar.gz
RUN cp -Rv /tmp/apache-tomcat-8.5.35/* /usr/local/tomcat/
EXPOSE 8080

ADD files /files

CMD ["/files/start.sh"]
