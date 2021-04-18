
import { App } from '@aws-cdk/core'

import { SampleStack } from './stack'

const app = new App()
new SampleStack(app, 'SampleStack')

const tpl = JSON.stringify(app.synth().stacks[0].template)

console.log(tpl)