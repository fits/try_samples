
import { App } from '@aws-cdk/core'
import { SdkProvider } from 'aws-cdk/lib/api/aws-auth'
import { CloudFormationDeployments } from 'aws-cdk/lib/api/cloudformation-deployments'

import { SampleStack } from './stack'

const app = new App()
new SampleStack(app, 'Sample1Stack')

const run = async () => {
    const deployer = new CloudFormationDeployments({
        sdkProvider: await SdkProvider.withAwsCliCompatibleDefaults()
    })

    const r = await deployer.deployStack({
        stack: app.synth().stacks[0],
        quiet: true
    })

    console.log(r)
}

run().catch(err => console.error(err))
