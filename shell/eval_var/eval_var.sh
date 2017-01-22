#!/bin/sh

TABLE=SAMPLE
ID=123

DATA=`cat sample.txt`

echo "$DATA"

DATA2=`eval echo $DATA`

echo "$DATA2"
