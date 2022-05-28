package main

import (
	"github.com/aws/aws-cdk-go/awscdk/v2"
	ec2 "github.com/aws/aws-cdk-go/awscdk/v2/awsec2"
	ecr "github.com/aws/aws-cdk-go/awscdk/v2/awsecr"
	ecs "github.com/aws/aws-cdk-go/awscdk/v2/awsecs"
	ecsptn "github.com/aws/aws-cdk-go/awscdk/v2/awsecspatterns"
	"github.com/aws/constructs-go/constructs/v10"
	"github.com/aws/jsii-runtime-go"
	"os"
)

type DemoAppStackProps struct {
	awscdk.StackProps
}

func NewDemoAppStack(scope constructs.Construct, id string, props *DemoAppStackProps) awscdk.Stack {
	var sprops awscdk.StackProps
	if props != nil {
		sprops = props.StackProps
	}
	stack := awscdk.NewStack(scope, &id, &sprops)

	vpc := ec2.NewVpc(stack, jsii.String("VPC"), &ec2.VpcProps{
		MaxAzs: jsii.Number(2),
	})

	cluster := ecs.NewCluster(stack, jsii.String("Cluster"), &ecs.ClusterProps{
		Vpc: vpc,
	})

	repo := ecr.Repository_FromRepositoryName(stack, jsii.String("Repository"), jsii.String("demo"))

	ecsptn.NewApplicationLoadBalancedFargateService(stack, jsii.String("FargateService"), &ecsptn.ApplicationLoadBalancedFargateServiceProps{
		Cluster: cluster,
		TaskImageOptions: &ecsptn.ApplicationLoadBalancedTaskImageOptions{
			Image: ecs.AssetImage_FromEcrRepository(repo, jsii.String("0.0.1-SNAPSHOT")),
			ContainerPort: jsii.Number(8080),
		},
	})

	stack.Tags().SetTag(jsii.String("project"), jsii.String("demo"), jsii.Number(0), jsii.Bool(true))

	return stack
}

func main() {
	app := awscdk.NewApp(nil)

	NewDemoAppStack(app, "DemoAppStack", &DemoAppStackProps{
		awscdk.StackProps{
			Env: env(),
		},
	})

	app.Synth(nil)
}

func env() *awscdk.Environment {
	return &awscdk.Environment{
		Account: jsii.String(os.Getenv("CDK_DEFAULT_ACCOUNT")),
		Region:  jsii.String(os.Getenv("CDK_DEFAULT_REGION")),
	}
}
