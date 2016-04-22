#!/bin/sh

i=1

while [ $i -le $1 ]
do
	sleep $2
	echo "run $i"

	i=`expr $i + 1`
done
