
import { send, SUCCESS } from 'cfn-response'

import {
    CloudFormationCustomResourceEvent as Event,
    Context
} from 'aws-lambda'

export const handler = (event: Event, context: Context) => {
    console.log(event)

    if (event.RequestType == 'Create') {
        console.log('create')
    }

    send(event, context, SUCCESS, {})
}
