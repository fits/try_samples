#!/bin/sh

BASE_DIR=`dirname $0`

java -cp $JAVA_HOME/lib/tools.jar:$BASE_DIR/thread_stat.jar sample.ThreadStat $*
