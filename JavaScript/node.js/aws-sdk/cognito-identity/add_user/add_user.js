
const { CognitoIdentityProviderClient, AdminCreateUserCommand } = require('@aws-sdk/client-cognito-identity-provider')

const region = process.env['REGION']
const userPoolId = process.env['USER_POOL_ID']

const email = process.argv[2]

if (!email) {
    console.log('required email')
    return
}

const client = new CognitoIdentityProviderClient({ region })

const run = async () => {

    const res = await client.send(new AdminCreateUserCommand({
        UserPoolId: userPoolId,
        Username: email
    }))

    console.log(res.User)
}

run().catch(err => console.error(err))
