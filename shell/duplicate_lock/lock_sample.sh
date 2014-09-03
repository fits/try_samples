#!/bin/sh

DIR=`dirname $0`
LOCK_FILE=$DIR/sample.lock

ln -s $0 $LOCK_FILE || exit

trap "rm $LOCK_FILE; exit" 1 2 3 15

echo sleep...
sleep 30s

rm $LOCK_FILE
exit
