
const generatePolicy = (user, arn) => {
    return {
        principalId: user,
        policyDocument: {
            Version: '2012-10-17',
            Statement: [
                {
                    Effect: 'Allow',
                    Action: 'execute-api:Invoke',
                    Resource: [ arn ]
                }
            ]
        }
    }
}

exports.handler = (event, context, callback) => {
    const token = event.authorizationToken

    if (token === 'logintest1') {
        callback(null, generatePolicy('user1', event.methodArn))
    }
    else {
        callback('unauthorized')
    }
}
