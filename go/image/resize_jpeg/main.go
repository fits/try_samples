package main

import (
	"image"
	"image/jpeg"
	"log"
	"os"
	"strconv"

	"golang.org/x/image/draw"
)

func main() {
	file := os.Args[1]
	width, _ := strconv.Atoi(os.Args[2])
	height, _ := strconv.Atoi(os.Args[3])

	reader, err := os.Open(file)

	if err != nil {
		log.Fatal(err)
	}

	defer reader.Close()

	img, _, err := image.Decode(reader)

	if err != nil {
		log.Fatal(err)
	}

	dst := image.NewNRGBA(image.Rect(0, 0, width, height))

	draw.NearestNeighbor.Scale(dst, dst.Rect, img, img.Bounds(), draw.Over, nil)

	output, err := os.Create("output.jpg")

	if err != nil {
		log.Fatal(err)
	}

	jpeg.Encode(output, dst, nil)
}
