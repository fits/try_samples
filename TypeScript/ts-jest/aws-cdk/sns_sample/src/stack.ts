
import { Construct, Stack, StackProps } from '@aws-cdk/core'
import * as sns from '@aws-cdk/aws-sns'

export class SampleStack extends Stack {
    constructor(scope: Construct, id: string, props?: StackProps) {
        super(scope, id, props)

        new sns.Topic(this, 'SampleTopic')
    }
}
