
import { App, Construct, Stack, StackProps, CfnOutput } from '@aws-cdk/core'
import * as dynamodb from '@aws-cdk/aws-dynamodb'

import { CfDeployer } from './deployer'

class SampleStack extends Stack {
    constructor(scope: Construct, id: string, props?: StackProps) {
        super(scope, id, props)

        const table = new dynamodb.Table(this, 'Items', {
            partitionKey: { name: 'id', type: dynamodb.AttributeType.STRING }
        })

        new CfnOutput(this, 'TableName', {
            value: table.tableName
        })
    }
}

const app = new App()

new SampleStack(app, 'SampleStack2', {
    env: { region: process.env.CDK_DEFAULT_REGION }
})

CfDeployer.deploy(app.synth().stacks[0])
    .then(r => console.log(r))
    .catch(err => console.error(err))