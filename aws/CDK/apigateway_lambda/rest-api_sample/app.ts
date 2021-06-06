import { App, Stack } from '@aws-cdk/core'
import { Runtime } from '@aws-cdk/aws-lambda'
import { NodejsFunction } from '@aws-cdk/aws-lambda-nodejs'
import { LambdaRestApi } from '@aws-cdk/aws-apigateway'

const app = new App()

const stack = new Stack(app, 'RestLambdaStack', {
    env: { region: process.env.CDK_DEFAULT_REGION }
})

const func = new NodejsFunction(stack, 'SampleFunc', {
    runtime: Runtime.NODEJS_14_X,
    entry: 'src/handler.ts'
})

new LambdaRestApi(stack, 'SampleApi', {
    handler: func
})
