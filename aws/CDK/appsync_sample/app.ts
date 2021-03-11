
import { App, Stack } from '@aws-cdk/core'
import * as appsync from '@aws-cdk/aws-appsync'
import * as lambda from '@aws-cdk/aws-lambda'
import * as dynamodb from '@aws-cdk/aws-dynamodb'

const app = new App()
const stack = new Stack(app, 'SampleStack')

const eventTable = new dynamodb.Table(stack, 'StockEvents', {
    partitionKey: { name: 'TenantStockId', type: dynamodb.AttributeType.STRING },
    sortKey: { name: 'Rev', type: dynamodb.AttributeType.NUMBER },
    stream: dynamodb.StreamViewType.NEW_IMAGE
})

const snapshotTable = new dynamodb.Table(stack, 'Stocks', {
    partitionKey: { name: 'TenantId', type: dynamodb.AttributeType.STRING },
    sortKey: { name: 'StockId', type: dynamodb.AttributeType.STRING }
})

const dbEnv = {
    'EVENT_TABLE': eventTable.tableName,
    'SNAPSHOT_TABLE': snapshotTable.tableName
}

const findFunc = new lambda.Function(stack, 'StockFind', {
    runtime: lambda.Runtime.NODEJS_14_X,
    handler: 'find.handler',
    code: lambda.Code.fromAsset('src/func'),
    environment: dbEnv
})

eventTable.grantReadData(findFunc)
snapshotTable.grantReadData(findFunc)

const assignFunc = new lambda.Function(stack, 'StockAssign', {
    runtime: lambda.Runtime.NODEJS_14_X,
    handler: 'assign.handler',
    code: lambda.Code.fromAsset('src/func'),
    environment: dbEnv
})

eventTable.grantReadWriteData(assignFunc)
snapshotTable.grantReadData(assignFunc)

const arriveFunc = new lambda.Function(stack, 'StockArrive', {
    runtime: lambda.Runtime.NODEJS_14_X,
    handler: 'arrive.handler',
    code: lambda.Code.fromAsset('src/func'),
    environment: dbEnv
})

eventTable.grantReadWriteData(arriveFunc)
snapshotTable.grantReadData(arriveFunc)

const shipFunc = new lambda.Function(stack, 'StockShip', {
    runtime: lambda.Runtime.NODEJS_14_X,
    handler: 'ship.handler',
    code: lambda.Code.fromAsset('src/func'),
    environment: dbEnv
})

eventTable.grantReadWriteData(shipFunc)
snapshotTable.grantReadData(shipFunc)

const api = new appsync.GraphqlApi(stack, 'StockApi', {
    name: 'stock',
    schema: appsync.Schema.fromAsset('src/api/schema.graphql'),
    authorizationConfig: {
        defaultAuthorization: {
            authorizationType: appsync.AuthorizationType.IAM
        }
    }
})

const requestMappingTemplate = appsync.MappingTemplate.fromString(`
    {
        "version": "2018-05-29",
        "operation": "Invoke",
        "payload": $utils.toJson($ctx.args.input)
    }
`)

const responseMappingTemplate = 
    appsync.MappingTemplate.fromString('$util.toJson($ctx.result)')

api.createResolver({
    typeName: 'Query',
    fieldName: 'find',
    dataSource: api.addLambdaDataSource('FindDataSource', findFunc),
    requestMappingTemplate,
    responseMappingTemplate
})

api.createResolver({
    typeName: 'Mutation',
    fieldName: 'assign',
    dataSource: api.addLambdaDataSource('AssignDataSource', assignFunc),
    requestMappingTemplate,
    responseMappingTemplate
})

api.createResolver({
    typeName: 'Mutation',
    fieldName: 'arrive',
    dataSource: api.addLambdaDataSource('ArriveDataSource', arriveFunc),
    requestMappingTemplate,
    responseMappingTemplate
})

api.createResolver({
    typeName: 'Mutation',
    fieldName: 'ship',
    dataSource: api.addLambdaDataSource('ShipDataSource', shipFunc),
    requestMappingTemplate,
    responseMappingTemplate
})
