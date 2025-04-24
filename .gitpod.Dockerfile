FROM gitpod/workspace-full

RUN sudo apt update && \
    sudo apt install -y openjdk-17-jdk maven

ENV JAVA_HOME=/usr/lib/jvm/java-17-openjdk-amd64
ENV PATH=$JAVA_HOME/bin:$PATH