package main

import (
	"context"
	"fmt"
	"os"
	"strconv"

	"github.com/go-mysql-org/go-mysql/mysql"
	"github.com/go-mysql-org/go-mysql/replication"
)

func main() {
	binlogfile := os.Args[1]
	position, _ := strconv.ParseUint(os.Args[2], 10, 32)

	cfg := replication.BinlogSyncerConfig{
		ServerID: 1,
		Host:     "127.0.0.1",
		Port:     3306,
		User:     "root",
		Password: "",
	}

	syncer := replication.NewBinlogSyncer(cfg)
	defer syncer.Close()

	pos := mysql.Position{Name: binlogfile, Pos: uint32(position)}

	streamer, _ := syncer.StartSync(pos)

	tmap := make(map[uint64]*replication.TableMapEvent)

	for {
		ev, _ := streamer.GetEvent(context.Background())
		ev.Dump(os.Stdout)

		switch t := ev.Event.(type) {
		case *replication.TableMapEvent:
			fmt.Printf("*** table: id=%d, name=%s, column_names=%s\n",
				t.TableID, t.Table, t.ColumnName)

			tmap[t.TableID] = t

		case *replication.RowsEvent:
			fmt.Printf("*** schema=%s, table=%s, rows=%s\n",
				tmap[t.TableID].Schema, tmap[t.TableID].Table, t.Rows)
		}
	}
}
