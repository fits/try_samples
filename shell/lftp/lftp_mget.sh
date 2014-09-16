#!/bin/sh

BACKUP_DIR="./backup/data_`date '+%Y%m%d%H%M%S'`"
FILE_PATTERN="data*.txt"

FTP_URL="ftp://xxx:xxx@localhost/xxx"

if [ ! -d "$BACKUP_DIR" ]; then
    mkdir -p $BACKUP_DIR
fi

lftp -c "open $FTP_URL; mget -E -O $BACKUP_DIR $FILE_PATTERN"

for f in `find $BACKUP_DIR -type f -name $FILE_PATTERN`
do
	echo $f
done

# rm -fr $BACKUP_DIR
