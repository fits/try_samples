
import { App, Stack, CustomResource, RemovalPolicy, Duration } from '@aws-cdk/core'
import { Runtime } from '@aws-cdk/aws-lambda'
import { NodejsFunction } from '@aws-cdk/aws-lambda-nodejs'
import { Domain, ElasticsearchVersion } from '@aws-cdk/aws-elasticsearch'
import * as ec2 from '@aws-cdk/aws-ec2'

const app = new App()

const stack = new Stack(app, 'CustomResourceSample', {
    env: {
        account: process.env.CDK_DEFAULT_ACCOUNT,
        region: process.env.CDK_DEFAULT_REGION
    }
})

const vpc = new ec2.Vpc(stack, 'VPC', {
    maxAzs: 1,
    subnetConfiguration: [
        { name: 'es', subnetType: ec2.SubnetType.PRIVATE_ISOLATED }
    ]
})

const vpcEndpoint = vpc.addGatewayEndpoint('CustomResourceResponse', {
    service: ec2.GatewayVpcEndpointAwsService.S3,
    subnets: [
        { subnetType: ec2.SubnetType.PRIVATE_ISOLATED }
    ]
})

const esGroup = new ec2.SecurityGroup(stack, 'EsGroup', {
    vpc
})

const lambdaGroup = new ec2.SecurityGroup(stack, 'LambdaGroup', {
    vpc
})

esGroup.addIngressRule(lambdaGroup, ec2.Port.tcp(80), 'allow lambda')

const es = new Domain(stack, 'Sample', {
    version: ElasticsearchVersion.V7_10,
    vpc,
    vpcSubnets: [
        { subnetType: ec2.SubnetType.PRIVATE_ISOLATED }
    ],
    securityGroups: [ esGroup ],
    removalPolicy: RemovalPolicy.DESTROY
})

const lambda = new NodejsFunction(stack, 'InitFunc', {
    runtime: Runtime.NODEJS_14_X,
    entry: 'src/index.ts',
    vpc,
    securityGroups: [ lambdaGroup ],
    environment: {
        ENDPOINT: es.domainEndpoint
    },
    timeout: Duration.minutes(5),
    retryAttempts: 0
})

es.grantReadWrite(lambda)

const rs = new CustomResource(stack, 'Init', {
    serviceToken: lambda.functionArn
})

rs.node.addDependency(vpcEndpoint)

vpc.isolatedSubnets.forEach(s => {
    rs.node.addDependency(s)
})
