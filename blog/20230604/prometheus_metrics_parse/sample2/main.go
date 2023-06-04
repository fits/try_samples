package main

import (
	"fmt"
	"log"
	"os"
	"strings"

	dto "github.com/prometheus/client_model/go"
	"github.com/prometheus/common/expfmt"
)

func joinLabel(ps []*dto.LabelPair) string {
	rs := []string{}

	for _, p := range ps {
		rs = append(rs, fmt.Sprintf("%s:%s", p.GetName(), p.GetValue()))
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

	for k, v := range mf {
		if v.GetType() == dto.MetricType_HISTOGRAM {
			for _, m := range v.GetMetric() {
				hist := m.GetHistogram()

				label := joinLabel(m.GetLabel())
				sum := hist.GetSampleSum()

				lastCount := uint64(0)

				for _, b := range hist.GetBucket() {
					count := b.GetCumulativeCount() - lastCount
					lastCount = b.GetCumulativeCount()

					if count > 0 {
						fmt.Printf(
							"name=%s, label=[%s], sum=%f, le=%f, count=%d \n",
							k, label, sum, b.GetUpperBound(), count,
						)
					}
				}
			}
		}
	}
}
