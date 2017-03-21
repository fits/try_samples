@echo off

set JAVA_OPTS="-Dakka.remote.netty.tcp.port=2051"

groovy server_sample.groovy
