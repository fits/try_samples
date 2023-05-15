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

	mf, err := p.TextToMetricFamilies(f)

	if err != nil {
		log.Fatal(err)
	}

	header := strings.Join([]string{
		"name",
		"label1",
		"label2",
		"label3",
		"label4",
		"label5",
		"sum",
		"le",
		"count",
	}, SEP)

	fmt.Println(header)

	for k, v := range mf {
		if v.GetType() == dto.MetricType_HISTOGRAM {
			for _, m := range v.GetMetric() {
				hist := m.GetHistogram()

				common := strings.Join([]string{
					k,
					labelValue(m.GetLabel(), 0),
					labelValue(m.GetLabel(), 1),
					labelValue(m.GetLabel(), 2),
					labelValue(m.GetLabel(), 3),
					labelValue(m.GetLabel(), 4),
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
