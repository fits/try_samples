
import * as cdk from 'aws-cdk-lib'
import * as s3 from 'aws-cdk-lib/aws-s3'
import * as lambda from 'aws-cdk-lib/aws-lambda'
import { NodejsFunction } from 'aws-cdk-lib/aws-lambda-nodejs'

const bucketName = 'sample1'

const app = new cdk.App()

const stack = new cdk.Stack(app, 'SampleSharpStack', {
    env: {
        account: process.env.CDK_DEFAULT_ACCOUNT,
        region: process.env.CDK_DEFAULT_REGION,
    }
})

const bucket = new s3.Bucket(stack, 'Bucket', {
    bucketName
})

const func = new NodejsFunction(stack, 'SharpLambda', {
    runtime: lambda.Runtime.NODEJS_16_X,
    entry: 'src/lambda/index.mjs',
    bundling: {
        nodeModules: ['sharp']
    },
    environment: {
        'BUCKET_NAME': bucketName
    },
    retryAttempts: 0,
    memorySize: 1024
})

bucket.grantRead(func)

const funcUrl = func.addFunctionUrl({
    authType: lambda.FunctionUrlAuthType.NONE
})

new cdk.CfnOutput(stack, 'Lambda URL', {
    value: funcUrl.url
})

app.synth()
