
const {
    CognitoIdentityProviderClient, 
    AdminCreateUserCommand, AdminDeleteUserCommand
} = require('@aws-sdk/client-cognito-identity-provider')

const {
    DynamoDBClient, PutItemCommand
} = require('@aws-sdk/client-dynamodb')

const { v4: uuidv4 } = require('uuid')

const tableName = process.env.TABLE_NAME
const userPoolId = process.env.USER_POOL_ID

const dynamodb = new DynamoDBClient()
const userPool = new CognitoIdentityProviderClient()

exports.handler = async (event) => {
    const email = event.email
    const password = event.password

    const storeId = `STORE-${uuidv4()}`

    const user = await userPool.send(new AdminCreateUserCommand({
        UserPoolId: userPoolId,
        Username: email,
        TemporaryPassword: password,
        UserAttributes: [
            { Name: 'custom:storeId', Value: storeId }
        ]
    }))

    const userName = user.User.Username

    try {
        await dynamodb.send(new PutItemCommand({
            TableName: tableName,
            Item: {
                storeId: { S: storeId },
                userPoolUserName: { S: userName },
                status: { S: 'active' },
            },
            ConditionExpression: 'attribute_not_exists(storeId)'
        }))
    } catch(e) {
        await userPool.send(new AdminDeleteUserCommand({
            UserPoolId: userPoolId,
            Username: userName
        }))

        throw e
    }

    return { storeId }
}