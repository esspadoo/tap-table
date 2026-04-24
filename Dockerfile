FROM tomcat:10.1.53-jdk25-temurin

RUN cp -r /usr/local/tomcat/webapps.dist/manager /usr/local/tomcat/webapps/manager
