
import * as cdk from 'aws-cdk-lib'
import * as lambda from 'aws-cdk-lib/aws-lambda'

const app = new cdk.App()

const stack = new cdk.Stack(app, 'CustomRuntimeStack', {
    env: {
        account: process.env.CDK_DEFAULT_ACCOUNT,
        region: process.env.CDK_DEFAULT_REGION,
    }
})

const layer = new lambda.LayerVersion(stack, 'LambdaLayer', {
    code: lambda.Code.fromAsset('./resources/lambda-layer'),
    removalPolicy: cdk.RemovalPolicy.DESTROY
})

const func = new lambda.Function(stack, 'Lambda', {
    runtime: lambda.Runtime.PROVIDED_AL2,
    code: lambda.Code.fromAsset('./resources/lambda'),
    handler: 'bootstrap',
    layers: [ layer ],
    retryAttempts: 0,
    memorySize: 1024
})

const funcUrl = func.addFunctionUrl({
    authType: lambda.FunctionUrlAuthType.NONE
})

new cdk.CfnOutput(stack, 'Lambda URL', {
    value: funcUrl.url
})

app.synth()
