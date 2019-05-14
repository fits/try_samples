#!/bin/bash

START=$1
END=$2
RATE=$3
DESTDIR=$4
PREFIX=$5
URL=$6

cvlc $URL --video-filter=scene --vout=vdummy --start-time=$START --stop-time=$END --scene-format=jpg --scene-ratio=$RATE --scene-prefix=$PREFIX --scene-path=$DESTDIR vlc://quit

