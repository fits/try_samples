
import { App } from '@aws-cdk/core'
import { SampleStack } from './src/stack'

const app = new App()
new SampleStack(app, 'SampleStack')
