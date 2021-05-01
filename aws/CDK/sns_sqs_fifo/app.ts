
import { App, Stack } from '@aws-cdk/core'
import * as sns from '@aws-cdk/aws-sns'
import * as sqs from '@aws-cdk/aws-sqs'
import { SqsSubscription } from '@aws-cdk/aws-sns-subscriptions'
import * as lambda from '@aws-cdk/aws-lambda'
import { NodejsFunction } from '@aws-cdk/aws-lambda-nodejs'
import { SqsEventSource } from '@aws-cdk/aws-lambda-event-sources'

const app = new App()

const stack = new Stack(app, 'FifoSampleStack', {
    env: { region: process.env.CDK_DEFAULT_REGION }
})

const topic = new sns.Topic(stack, 'FifoTopic', {
    fifo: true,
    topicName: 'FifoTopic'
})

const queue = new sqs.Queue(stack, 'FifoQueue', {
    fifo: true
})

topic.addSubscription(new SqsSubscription(queue))

const func = new NodejsFunction(stack, 'DebugOutput', {
    runtime: lambda.Runtime.NODEJS_14_X,
    entry: 'src/index.js'
})

func.addEventSource(new SqsEventSource(queue))
