
import { App, Stack } from '@aws-cdk/core'
import * as events from '@aws-cdk/aws-events'
import { CloudWatchLogGroup } from '@aws-cdk/aws-events-targets'
import * as logs from '@aws-cdk/aws-logs'

const account = process.env.CDK_DEFAULT_ACCOUNT ?? ''

const app = new App()
const stack = new Stack(app, 'SampleStack')

const bus = new events.EventBus(stack, 'SampleBus')

const ptn: events.EventPattern = { account: [ account ] }

const rule = new events.Rule(stack, 'SampleRule', {
    eventBus: bus,
    eventPattern: ptn
})

const logGroup = new logs.LogGroup(stack, 'SampleLogs')

rule.addTarget(new CloudWatchLogGroup(logGroup))
