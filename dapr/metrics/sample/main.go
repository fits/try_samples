package main

import (
	"fmt"
	"log"
	"os"
	"strings"

	dto "github.com/prometheus/client_model/go"
	"github.com/prometheus/common/expfmt"
)

func labelValue(ds []*dto.LabelPair, index int) string {
	if len(ds) >= index+1 {
		return ds[index].GetValue()
	}

	return ""
}

const SEP = "\t"

func main() {
	file := os.Args[1]
	f, err := os.Open(file)

	if err != nil {
		log.Fatal(err)
	}
	defer f.Close()

	p := expfmt.TextParser{}

	m, err := p.TextToMetricFamilies(f)

	if err != nil {
		log.Fatal(err)
	}

	names := []string{"dapr_grpc_io_client_roundtrip_latency", "dapr_grpc_io_server_server_latency", "dapr_http_client_roundtrip_latency", "dapr_http_server_latency"}

	header := strings.Join([]string{
		"name",
		"label1",
		"label2",
		"label3",
		"label4",
		"sum",
		"le",
		"count",
	}, SEP)

	fmt.Println(header)

	for _, name := range names {
		mf := m[name]

		if mf != nil {
			for _, gc := range mf.GetMetric() {
				hist := gc.GetHistogram()

				common := strings.Join([]string{
					name,
					labelValue(gc.GetLabel(), 0),
					labelValue(gc.GetLabel(), 1),
					labelValue(gc.GetLabel(), 2),
					labelValue(gc.GetLabel(), 3),
					fmt.Sprintf("%f", hist.GetSampleSum()),
				}, SEP)

				lastCount := uint64(0)

				for _, b := range hist.GetBucket() {

					count := b.GetCumulativeCount() - lastCount
					lastCount = b.GetCumulativeCount()

					if count > 0 {
						data := strings.Join([]string{
							common,
							fmt.Sprintf("%.f", b.GetUpperBound()),
							fmt.Sprintf("%d", count),
						}, SEP)

						fmt.Println(data)
					}
				}
			}
		}
	}
}
