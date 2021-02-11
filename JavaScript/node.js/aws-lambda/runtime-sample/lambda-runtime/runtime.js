
const fetch = require('node-fetch')

const runtimeUrl = `http://${process.env["AWS_LAMBDA_RUNTIME_API"]}/2018-06-01/runtime/invocation`

const appRoot = process.cwd()
const [moduleName, handlerName] = process.argv[2].split('.')

const app = require(`${appRoot}/${moduleName}`)

const envData = {
    functionVersion: process.env["AWS_LAMBDA_FUNCTION_VERSION"],
    functionName: process.env["AWS_LAMBDA_FUNCTION_NAME"],
    memoryLimitInMB: process.env["AWS_LAMBDA_FUNCTION_MEMORY_SIZE"],
    logGroupName: process.env["AWS_LAMBDA_LOG_GROUP_NAME"],
    logStreamName: process.env["AWS_LAMBDA_LOG_STREAM_NAME"],
}

const run = async () => {
    const nextRes = await fetch(`${runtimeUrl}/next`)

    const deadline = parseInt(nextRes.headers.get('lambda-runtime-deadline-ms'))

    const context = Object.assign(
        {
            getRemainingTimeInMillis: () => deadline - Date.now(),
            awsRequestId: nextRes.headers.get('lambda-runtime-aws-request-id'),
            invokedFunctionArn: 
                nextRes.headers.get('lambda-runtime-invoked-function-arn'),
            identity: {},
            clientContext: {},
        }, 
        envData
    )

    try {
        const event = await nextRes.json()

        const res = await app[handlerName](event, context)
        console.log(`* handler result: ${res}`)

        await fetch(
            `${runtimeUrl}/${context.awsRequestId}/response`, 
            {method: 'POST', body: res}
        )

    } catch (e) {
        await fetch(
            `${runtimeUrl}/${context.awsRequestId}/error`, 
            {method: 'POST', body: JSON.stringify({type: e.type, message: e.message})}
        )
    }
}

const loop = async () => {
    while (true) {
        await run()
    }
}

loop().catch(err => console.error(err))
