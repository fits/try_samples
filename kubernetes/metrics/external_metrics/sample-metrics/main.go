package main

import (
	"context"
	"log"

	"k8s.io/apimachinery/pkg/api/resource"
	metav1 "k8s.io/apimachinery/pkg/apis/meta/v1"
	"k8s.io/apimachinery/pkg/labels"
	"k8s.io/apimachinery/pkg/util/wait"
	"k8s.io/metrics/pkg/apis/external_metrics"
	basecmd "sigs.k8s.io/custom-metrics-apiserver/pkg/cmd"
	"sigs.k8s.io/custom-metrics-apiserver/pkg/provider"
)

type SampleProvider struct {}

func (p *SampleProvider) GetExternalMetric(ctx context.Context, namespace string, metricSelector labels.Selector, info provider.ExternalMetricInfo) (*external_metrics.ExternalMetricValueList, error) {
	name := info.Metric

	items := []external_metrics.ExternalMetricValue{
		{
			MetricName: name,
			MetricLabels: map[string]string{
				"app": "sample",
				"category": "1",
			},
			Timestamp: metav1.Now(),
			Value: *resource.NewQuantity(123, resource.DecimalSI),
		},
		{
			MetricName: name,
			MetricLabels: map[string]string{
				"app": "sample",
				"category": "2",
			},
			Timestamp: metav1.Now(),
			Value: *resource.NewScaledQuantity(456, resource.Micro),
		},
	}
	
	return &external_metrics.ExternalMetricValueList{
		Items: items,
	}, nil
}

func (p *SampleProvider) ListAllExternalMetrics() []provider.ExternalMetricInfo {
	return []provider.ExternalMetricInfo{
		{
			Metric: "sample1",
		},
		{
			Metric: "sample2",
		},
	}
}

func main() {
	cmd := basecmd.AdapterBase{}

	provider := SampleProvider{}

	cmd.WithExternalMetrics(&provider)

	if err := cmd.Run(wait.NeverStop); err != nil {
		log.Fatal(err)
	}
}
