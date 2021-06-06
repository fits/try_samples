import { App, Stack, CfnOutput } from '@aws-cdk/core'
import { Runtime } from '@aws-cdk/aws-lambda'
import { NodejsFunction } from '@aws-cdk/aws-lambda-nodejs'
import { HttpApi } from '@aws-cdk/aws-apigatewayv2'
import { LambdaProxyIntegration } from '@aws-cdk/aws-apigatewayv2-integrations'

const app = new App()

const stack = new Stack(app, 'HttpLambdaStack', {
    env: { region: process.env.CDK_DEFAULT_REGION }
})

const func = new NodejsFunction(stack, 'SampleFunc', {
    runtime: Runtime.NODEJS_14_X,
    entry: 'src/handler.ts'
})

const api = new HttpApi(stack, 'SampleApi')

api.addRoutes({
    path: '/',
    integration: new LambdaProxyIntegration({ handler: func })
})

new CfnOutput(stack, 'ApiUrl', {
    value: api.url ?? ''
})
