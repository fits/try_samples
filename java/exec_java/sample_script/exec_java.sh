#!/bin/sh

APP_CLASS=SampleApp

BASE_DIR=`dirname $_`

CP=$BASE_DIR
LIB=$BASE_DIR/lib

for jar in $LIB/*.jar; do CP=$CP:$jar; done

java -cp $CP $APP_CLASS $*
