
import { App, Stack, CfnOutput } from '@aws-cdk/core'
import * as cognito from '@aws-cdk/aws-cognito'

const app = new App()

const stack = new Stack(app, 'SampleStack', {
    env: { region: process.env.CDK_DEFAULT_REGION }
})

const pool = new cognito.UserPool(stack, 'SampleUser', {
    signInAliases: {
        email: true
    },
    accountRecovery: cognito.AccountRecovery.NONE,
    customAttributes: {
        'test-id': new cognito.StringAttribute()
    }
})

new CfnOutput(stack, 'UserPoolId', {
    value: pool.userPoolId
})

new cognito.CfnUserPoolUser(stack, 'DefaultUser', {
    userPoolId: pool.userPoolId,
    username: 'user1@example.com',
    userAttributes: [
        {name: 'custom:test-id', value: 'test1'}
    ]
})
