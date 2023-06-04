package main

import (
	"fmt"
	"log"
	"strings"

	"github.com/prometheus/common/expfmt"
)

func main() {
	data := `
sample_total 10
test_metric{label="t1", proc="a1"} 123.4
`

	p := expfmt.TextParser{}

	mf, err := p.TextToMetricFamilies(strings.NewReader(data))

	if err != nil {
		log.Fatal(err)
	}

	for k, v := range mf {
		fmt.Printf("key=%s, name=%s, type=%s \n", k, v.GetName(), v.GetType())

		for _, l := range v.GetMetric()[0].Label {
			fmt.Printf("label name=%s, value=%s \n", l.GetName(), l.GetValue())
		}

		fmt.Printf("untyped value=%f \n", v.GetMetric()[0].Untyped.GetValue())

		fmt.Println("---")
	}
}
