
import { Construct, Stack, StackProps, CfnOutput } from '@aws-cdk/core'
import * as dynamodb from '@aws-cdk/aws-dynamodb'

export class SampleStack extends Stack {
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
