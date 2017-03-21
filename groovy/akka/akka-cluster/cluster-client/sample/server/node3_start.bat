@echo off

set JAVA_OPTS="-Dakka.remote.netty.tcp.port=2053"

groovy server_sample.groovy
