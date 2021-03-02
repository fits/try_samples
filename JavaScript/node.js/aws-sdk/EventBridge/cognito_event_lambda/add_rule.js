
const { EventBridgeClient, PutRuleCommand, PutTargetsCommand } = require('@aws-sdk/client-eventbridge')
const { LambdaClient, AddPermissionCommand } = require('@aws-sdk/client-lambda')

const region = process.env['REGION']

const ruleName = process.argv[2]
const lambdaName = process.argv[3]

const eventbridge = new EventBridgeClient({ region })
const lambda = new LambdaClient({ region })

const ptn = {
    source: ['aws.cognito-idp'],
    detail: {
        eventName: ['AdminCreateUser']
    }
}

const run = async () => {
    const r1 = await eventbridge.send(new PutRuleCommand({
        Name: ruleName,
        EventPattern: JSON.stringify(ptn)
    }))

    console.log(r1)

    const ruleArn = r1.RuleArn

    const r2 = await lambda.send(new AddPermissionCommand({
        Action: 'lambda:InvokeFunction',
        FunctionName: lambdaName,
        Principal: 'events.amazonaws.com',
        SourceArn: ruleArn,
        StatementId: 'EventBridge-sample-1',
    }))

    console.log(r2)

    const policy = JSON.parse(r2.Statement)
    const lambdaArn = policy.Resource

    console.log(`lambda arn: ${lambdaArn}`)

    const r3 = await eventbridge.send(new PutTargetsCommand({
        Rule: ruleName,
        Targets: [
            { Id: '1', Arn: lambdaArn }
        ]
    }))

    console.log(r3)

    if (r3.FailedEntryCount == 0) {
        console.log('success')
    }
}

run().catch(err => console.error(err))
