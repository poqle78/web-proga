FROM icr.io/appcafe/open-liberty:full-java21-openj9-ubi-minimal
COPY src/main/liberty/config/server.xml /config/server.xml
COPY target/student-service.war /config/apps/
COPY target/liberty/wlp/usr/shared/resources/ /opt/ol/wlp/usr/shared/resources/
