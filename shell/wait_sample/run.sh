#!/bin/sh

LIST="a b c"

for i in $LIST; do
	echo "start $i"

	source ./$i.sh &
done

wait

echo "end run"
