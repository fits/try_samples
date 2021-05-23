
import { App, Construct, Stack, StackProps, CfnOutput } from '@aws-cdk/core'
import * as dynamodb from '@aws-cdk/aws-dynamodb'
import * as lambda from '@aws-cdk/aws-lambda'
import { NodejsFunction } from '@aws-cdk/aws-lambda-nodejs'

class SampleStack extends Stack {
    constructor(scope: Construct, id: string, props?: StackProps) {
        super(scope, id, props)

        const table = new dynamodb.Table(this, 'Items', {
            partitionKey: { name: 'id', type: dynamodb.AttributeType.STRING}
        })

        const func = new NodejsFunction(this, 'SampleFunc', {
            runtime: lambda.Runtime.NODEJS_14_X,
            entry: './src/handler.ts',
            environment: {
                'TABLE_NAME': table.tableName
            }
        })

        table.grantWriteData(func)

        new CfnOutput(this, 'functionName', {
            value: func.functionName
        })
    }
}

const app = new App()

new SampleStack(app, 'SampleStack')
