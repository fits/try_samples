
const { CognitoIdentityProviderClient, ListUsersCommand } = require('@aws-sdk/client-cognito-identity-provider')

const region = process.env['REGION']
const userPoolId = process.env['USER_POOL_ID']

const client = new CognitoIdentityProviderClient({ region })

const run = async () => {
    const cmd = new ListUsersCommand({ UserPoolId: userPoolId })

    const res = await client.send(cmd)

    console.log(`pagination token: ${res.PaginationToken}`)

    res.Users.forEach(u => console.log(u))
}

run().catch(err => console.error(err))
