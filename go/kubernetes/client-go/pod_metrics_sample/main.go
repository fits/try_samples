package main

import (
	"context"
	"fmt"
	"log"
	"os"

	"k8s.io/client-go/kubernetes/scheme"
	"k8s.io/client-go/rest"
	"k8s.io/client-go/tools/clientcmd"
	"k8s.io/metrics/pkg/apis/metrics/v1beta1"
)

func main() {
	kubeconfig := os.Getenv("KUBECONFIG")
	config, err := clientcmd.BuildConfigFromFlags("", kubeconfig)

	if err != nil {
		log.Fatal(err)
	}

	config.GroupVersion = &v1beta1.SchemeGroupVersion
	config.APIPath = "/apis"
	config.NegotiatedSerializer = scheme.Codecs.WithoutConversion()

	client, err := rest.RESTClientFor(config)

	if err != nil {
		log.Fatal(err)
	}

	ctx := context.Background()

	ms := v1beta1.PodMetricsList{}

	err = client.Get().Resource("pods").Do(ctx).Into(&ms)

	if err != nil {
		log.Fatal(err)
	}

	for _, m := range ms.Items {
		fmt.Printf("pod metrics name=%s, namespace=%s, window=%v \n", m.Name, m.Namespace, m.Window)

		for _, c := range m.Containers {
			fmt.Printf("container name=%s, cpu=%v, memory=%v \n", c.Name, c.Usage.Cpu(), c.Usage.Memory())
		}

		fmt.Println("---")
	}
}
