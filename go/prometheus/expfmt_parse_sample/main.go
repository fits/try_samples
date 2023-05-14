package main

import (
	"fmt"
	"log"
	"os"

	"github.com/prometheus/common/expfmt"
)

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

	info := m["coredns_build_info"]

	for _, l := range info.GetMetric()[0].Label {
		fmt.Printf("%s: name=%s, value=%s \n", info.GetName(), l.GetName(), l.GetValue())
	}

	println("-----")

	cache_hits := m["coredns_cache_hits_total"].GetMetric()[0].GetCounter()

	fmt.Printf("coredns_cache_hits_total=%f \n", cache_hits.GetValue())

	println("-----")

	req_dur := m["coredns_dns_request_duration_seconds"].GetMetric()[0]

	for _, l := range req_dur.GetLabel() {
		fmt.Printf("label name=%s, value=%s \n", l.GetName(), l.GetValue())
	}

	hist := req_dur.GetHistogram()

	for _, b := range hist.GetBucket() {
		fmt.Printf("upperBound=%f, count=%d\n", b.GetUpperBound(), b.GetCumulativeCount())
	}

	fmt.Printf("sum=%f, count=%d \n", hist.GetSampleSum(), hist.GetSampleCount())
	fmt.Printf("sum / count = %f \n", hist.GetSampleSum()/float64(hist.GetSampleCount()))
}
