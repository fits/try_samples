#!/bin/sh

export JAVA_HOME=/cygdrive/c/jdk1.6.0_21
export HADOOP_HOME=/cygdrive/c/hadoop-0.21.0

export PATH=$HADOOP_HOME/bin:$JAVA_HOME/bin:$PATH

export HADOOP_CLASSPATH=project/boot/scala-2.8.0/lib/scala-library.jar

hadoop jar target/scala_2.8.0/moneycounter_2.8.0-1.0.jar work/testfile work/testoutput
