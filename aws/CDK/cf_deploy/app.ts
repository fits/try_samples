
import { App, Construct, Stack, StackProps, CfnOutput } from '@aws-cdk/core'
import * as dynamodb from '@aws-cdk/aws-dynamodb'

import { SdkProvider } from 'aws-cdk/lib/api/aws-auth'
import { CloudFormationDeployments } from 'aws-cdk/lib/api/cloudformation-deployments'

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

new SampleStack(app, 'SampleStack', {
    env: { region: process.env.CDK_DEFAULT_REGION }
})

const run = async () => {
    const deployer = new CloudFormationDeployments({
        sdkProvider: await SdkProvider.withAwsCliCompatibleDefaults()
    })

    const r = await deployer.deployStack({
        stack: app.synth().stacks[0],
        quiet: true
    })

    console.log(r)

    if (r.noOp) {
        console.log('*** NO CHANGE')
    }

    console.log(r.outputs.TableName)
}

run().catch(err => {
    console.log('*** ERROR')
    console.error(err)
})
