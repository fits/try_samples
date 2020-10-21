package main

import (
	"context"
	"fmt"
	"path/filepath"

	matav1 "k8s.io/apimachinery/pkg/apis/meta/v1"
	"k8s.io/client-go/kubernetes"
	"k8s.io/client-go/tools/clientcmd"
	"k8s.io/client-go/util/homedir"
)

func main() {
	home := homedir.HomeDir()
	kubeconfig := filepath.Join(home, ".kube", "config")

	//fmt.Println(kubeconfig)

	config, err := clientcmd.BuildConfigFromFlags("", kubeconfig)

	if err != nil {
		panic(err)
	}

	clientset, err := kubernetes.NewForConfig(config)

	if err != nil {
		panic(err)
	}

	pods, err := clientset.
		CoreV1().
		Pods("default").
		List(context.TODO(), matav1.ListOptions{})

	if err != nil {
		panic(err)
	}

	fmt.Println(pods)
}
