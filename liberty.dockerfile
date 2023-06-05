FROM amazoncorretto:17

RUN yum install libudev-devel -y

WORKDIR /JAR	

COPY captura-componentes-liberty-co-1.0-SNAPSHOT-jar-with-dependencies.jar /JAR

COPY libs/libudev.so.1 /usr/lib/libudev.so.1
COPY libs/libudev.so.1.7.2 / usr/lib/libudev.1.7.2
RUN ln -s /usr/lib/libudev.so.1 /usr/lib/libudev.so
CMD java -jar captura-componentes-liberty-co-1.0-SNAPSHOT-jar-with-dependencies.jar
