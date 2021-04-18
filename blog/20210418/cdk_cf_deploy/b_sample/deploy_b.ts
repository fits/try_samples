
import { App } from '@aws-cdk/core'

import { CfDeployer } from './deployer'
import { SampleStack } from './stack'

const app = new App()
new SampleStack(app, 'Sample2Stack')

CfDeployer.deploy(app.synth().stacks[0])
    .then(r => console.log(r))
    .catch(err => console.error(err))
