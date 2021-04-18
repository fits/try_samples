
import { expect as expectCDK, haveResource } from '@aws-cdk/assert'
import { App } from '@aws-cdk/core'

import { SampleStack } from '../src/stack'

test('SNS Topic Created', () => {
    const app = new App()
    const stack = new SampleStack(app, 'SampleStack')

    expectCDK(stack).to(haveResource('AWS::SNS::Topic'))
})