
import { App, Stack } from '@aws-cdk/core'
import * as lambda from '@aws-cdk/aws-lambda'

const app = new App()
const stack = new Stack(app, 'CdkLambdaSampleStack')

new lambda.Function(stack, 'SampleFunc', {
    runtime: lambda.Runtime.NODEJS_14_X,
    handler: 'index.handler',
    code: lambda.Code.fromAsset('src/sample_func')
})
