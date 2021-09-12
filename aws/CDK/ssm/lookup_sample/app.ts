
import { App, Stack, CfnOutput } from '@aws-cdk/core'
import { StringParameter } from '@aws-cdk/aws-ssm'

const paramName = '/sample/data'

const app = new App()

const stack = new Stack(app, 'SsmValueSample', {
    env: {
        account: process.env.CDK_DEFAULT_ACCOUNT,
        region: process.env.CDK_DEFAULT_REGION
    }
})

new CfnOutput(stack, 'valueForStringParameter', {
    value: StringParameter.valueForStringParameter(stack, paramName)
})

new CfnOutput(stack, 'valueFromLookup', {
    value: StringParameter.valueFromLookup(stack, paramName)
})
