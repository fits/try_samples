package main

import (
	"fmt"
	"log"
	"os"
	"strings"

	dto "github.com/prometheus/client_model/go"
	"github.com/prometheus/common/expfmt"
)

const SEP = "\t"

func joinLabel(ps []*dto.LabelPair) string {
	rs := []string{}

	for _, p := range ps {
		rs = append(rs, fmt.Sprintf("%s=%s", p.GetName(), p.GetValue()))
	}

	return strings.Join(rs, ",")
}

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
		"label",
		"sum",
		"le",
		"count",
	}, SEP)

	fmt.Println(header)

	for k, v := range mf {
		if v.GetType() == dto.MetricType_HISTOGRAM {
			for _, m := range v.GetMetric() {
				hist := m.GetHistogram()

				label := joinLabel(m.GetLabel())
				sum := fmt.Sprintf("%f", hist.GetSampleSum())

				lastCount := uint64(0)

				for _, b := range hist.GetBucket() {
					count := b.GetCumulativeCount() - lastCount
					lastCount = b.GetCumulativeCount()

					if count > 0 {
						data := strings.Join([]string{
							k,
							label,
							sum,
							fmt.Sprintf("%f", b.GetUpperBound()),
							fmt.Sprintf("%d", count),
						}, SEP)

						fmt.Println(data)
					}
				}
			}
		}
	}
}
