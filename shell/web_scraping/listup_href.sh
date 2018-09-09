#!/bin/sh

URL=$1

CONTENT=$(curl -s $URL)

RES=$(echo $CONTENT | grep -oP "a href=\"\K([^\"]*)")

for u in $RES; do
  echo $u
done
