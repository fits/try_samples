
import { App, Stack, CustomResource } from '@aws-cdk/core'
import { Runtime } from '@aws-cdk/aws-lambda'
import { NodejsFunction } from '@aws-cdk/aws-lambda-nodejs'

const app = new App()

const stack = new Stack(app, 'CustomResourceSample', {
    env: {
        region: process.env.CDK_DEFAULT_REGION
    }
})

const lambda = new NodejsFunction(stack, 'SampleFunc', {
    runtime: Runtime.NODEJS_14_X,
    entry: 'src/index.ts'
})

new CustomResource(stack, 'SampleResource', {
    serviceToken: lambda.functionArn
})
