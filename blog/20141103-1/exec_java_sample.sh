#!/bin/sh

APP_CLASS=sample.SampleApp

BASE_DIR=`dirname $0`

CP=$BASE_DIR
LIB=$BASE_DIR/lib

for jar in $LIB/*.jar; do CP=$CP:$jar; done

java -cp $CP $APP_CLASS $*
