#!/bin/sh

BACKUP_DIR="./backup/data_`date '+%Y%m%d%H%M%S'`"
FILE_PATTERN="data*.txt"

FTP_URL="ftp://xxx:xxx@localhsot/xxx"

get_file() {
    if [ ! -d "$BACKUP_DIR" ]; then
        mkdir -p $BACKUP_DIR
    fi

    lftp -c "open $FTP_URL; mget -E -O $BACKUP_DIR $FILE_PATTERN"

    # 最後のファイルを返す
    echo `find $BACKUP_DIR -type f -name $FILE_PATTERN | tail -n 1`
}

TRG_FILE=`get_file`

if [ ! -f "$TRG_FILE" ]; then

    rm -fr $BACKUP_DIR

    echo "file not found: $TRG_FILE"
    exit 1
fi

echo "ok: $TRG_FILE"
