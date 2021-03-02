
import { App, Stack } from '@aws-cdk/core'
import * as lambda from '@aws-cdk/aws-lambda'
import * as events from '@aws-cdk/aws-events'
import { LambdaFunction } from '@aws-cdk/aws-events-targets'

const app = new App()
const stack = new Stack(app, 'CdkSampleLambdaStack')

const func = new lambda.Function(stack, 'SampleFunc', {
    runtime: lambda.Runtime.NODEJS_14_X,
    handler: 'index.handler',
    code: lambda.Code.fromAsset('src/sample_func')
})

const bus = new events.EventBus(stack, 'SampleBus', {
    eventBusName: 'sample'
})

const ptn: events.EventPattern = { source: ['sample1'] }

const rule = new events.Rule(stack, 'SampleRule', {
    eventBus: bus,
    eventPattern: ptn
})

rule.addTarget(new LambdaFunction(func))
