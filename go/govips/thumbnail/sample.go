package main

import (
	"fmt"
	"os"
	"log"
	"strconv"
	"time"
	"github.com/davidbyttow/govips/v2/vips"
)

func main() {
	file := os.Args[1]
	width, _  := strconv.Atoi(os.Args[2])
	height, _  := strconv.Atoi(os.Args[3])

	start := time.Now()

	vips.LoggingSettings(nil, vips.LogLevelError)

	vips.Startup(nil)
	defer vips.Shutdown()

	img, err := vips.NewImageFromFile(file)

	if err != nil {
		log.Fatal(err)
	}

	img.Thumbnail(width, height, vips.InterestingNone)

	ep := vips.NewDefaultJPEGExportParams()
	buf, _, err := img.Export(ep)
	
	err = os.WriteFile("output.jpg", buf, 0644)

	if err != nil {
		log.Fatal(err)
	}

	fmt.Printf("time: %v ms\n", time.Since(start).Milliseconds())
}