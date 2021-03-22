
import { App, Stack, Construct, StackProps } from '@aws-cdk/core'
import * as iam from '@aws-cdk/aws-iam'
import * as cognito from '@aws-cdk/aws-cognito'
import * as dynamodb from '@aws-cdk/aws-dynamodb'
import * as lambda from '@aws-cdk/aws-lambda'
import { NodejsFunction } from '@aws-cdk/aws-lambda-nodejs'

class StoreStack extends Stack {
    constructor(scope: Construct, id: string, props?: StackProps) {
        super(scope, id, props)

        const pool = new cognito.UserPool(this, 'Users', {
            userPoolName: 'Users',
            signInAliases: {
                email: true
            },
            accountRecovery: cognito.AccountRecovery.NONE,
            customAttributes: {
                'storeId': new cognito.StringAttribute()
            }
        })

        pool.addClient('login', {
            preventUserExistenceErrors: true
        })

        const table = new dynamodb.Table(this, 'Stores', {
            tableName: 'Stores',
            partitionKey: { name: 'storeId', type: dynamodb.AttributeType.STRING}
        })

        const indexName = 'UserNameIndex'

        table.addGlobalSecondaryIndex({
            indexName,
            partitionKey: {
                name: 'userPoolUserName', type: dynamodb.AttributeType.STRING
            }
        })

        const func = new NodejsFunction(this, 'UserRegist', {
            functionName: 'UserRegist',
            runtime: lambda.Runtime.NODEJS_14_X,
            entry: 'src/lambda/index.js',
            environment: {
                'TABLE_NAME': table.tableName,
                'USER_POOL_ID': pool.userPoolId
            }
        })

        table.grantReadWriteData(func)

        func.addToRolePolicy(new iam.PolicyStatement({
            resources: [ pool.userPoolArn ],
            actions: [
                'cognito-idp:AdminCreateUser', 
                'cognito-idp:AdminDeleteUser'
            ]
        }))
    }
}

const app = new App()

new StoreStack(app, 'StoreStack', {
    env: { region: process.env.CDK_DEFAULT_REGION }
})
