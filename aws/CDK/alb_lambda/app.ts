import { App, Stack, CfnOutput } from '@aws-cdk/core'
import { Runtime } from '@aws-cdk/aws-lambda'
import { NodejsFunction } from '@aws-cdk/aws-lambda-nodejs'
import { Vpc } from '@aws-cdk/aws-ec2'
import { ApplicationLoadBalancer } from '@aws-cdk/aws-elasticloadbalancingv2'
import { LambdaTarget } from '@aws-cdk/aws-elasticloadbalancingv2-targets'

const app = new App()

const stack = new Stack(app, 'AlbLambdaStack', {
    env: { region: process.env.CDK_DEFAULT_REGION }
})

const func = new NodejsFunction(stack, 'SampleFunc', {
    runtime: Runtime.NODEJS_14_X,
    entry: 'src/handler.ts'
})

const vpc = new Vpc(stack, 'SampleVpc')

const alb = new ApplicationLoadBalancer(stack, 'SampleAlb', {
    vpc,
    internetFacing: true
})

const listener = alb.addListener('Listener', { port: 8080 })

listener.addTargets('Targets', {
    targets: [ new LambdaTarget(func) ]
})

new CfnOutput(stack, 'ApiDomain', {
    value: alb.loadBalancerDnsName
})
