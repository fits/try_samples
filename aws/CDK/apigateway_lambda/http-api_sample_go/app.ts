import { App, Stack, CfnOutput } from '@aws-cdk/core'
import { GoFunction } from '@aws-cdk/aws-lambda-go'
import { HttpApi } from '@aws-cdk/aws-apigatewayv2'
import { LambdaProxyIntegration } from '@aws-cdk/aws-apigatewayv2-integrations'

const app = new App()

const stack = new Stack(app, 'HttpGoLambdaStack', {
    env: { region: process.env.CDK_DEFAULT_REGION }
})

const func = new GoFunction(stack, 'SampleFunc', {
    entry: 'src/handler'
})

const api = new HttpApi(stack, 'SampleApi')

api.addRoutes({
    path: '/',
    integration: new LambdaProxyIntegration({ handler: func })
})

new CfnOutput(stack, 'ApiUrl', {
    value: api.url ?? ''
})
