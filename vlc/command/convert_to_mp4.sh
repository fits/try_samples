#!/bin/bash

URL=$1
DESTFILE=$2

cvlc $URL --no-repeat --no-loop --sout="#transcode{vcodec=h264,acodec=mp3,ab=192,channels=2}:std{access=file,mux=mp4,dst='$DESTFILE'}" vlc://quit

