FROM openjdk:11.0-jdk-slim-stretch

ARG DEPENDENCY=target

COPY ${DEPENDENCY}/pm-load-prescription-1.0.1.jar /pm-load-prescription-1.0.1.jar
 
##Agente Dynatrace
#RUN apt-get update
#RUN apt-get install -y sshpass

#RUN sshpass -p ColsZuaPP201928*# scp -o StrictHostKeyChecking=no zuserapp@200.31.20.99:/sftp/dynatrace/installer.sh /tmp/
#RUN ls -l /tmp

#RUN bash /tmp/installer.sh /home
#ENV LD_PRELOAD /home/dynatrace/oneagent/agent/lib64/liboneagentproc.so

#RUN rm /tmp/installer.sh
#RUN ls -l /tmp

ENTRYPOINT   [ "java","-jar","-Dspring.profiles.active=release","/pm-load-prescription-1.0.1.jar" ]