@echo off

set JAVA_OPTS="-Dakka.remote.netty.tcp.port=2052"

groovy sample.groovy
