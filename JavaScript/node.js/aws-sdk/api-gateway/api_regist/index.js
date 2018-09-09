
const AWS = require('aws-sdk')

const region = 'us-east-1'

const apigateway = new AWS.APIGateway({
    endpoint: 'http://127.0.0.1:4567/',
    region: region
})

const name = process.argv[2]
const path = process.argv[3]
const lambdaArn = process.argv[4]
const stage = process.argv[5]

const dump = r => {
    console.log(r)
    return r
}

const createApi = name => {
    const params = {
        name: name
    }

    return apigateway.createRestApi(params).promise()
                    .then(r => new Object({api_id: r.id}))
}

const createResource = path => info => {
    const rootParams = {
        restApiId: info.api_id
    }

    return apigateway.getResources(rootParams).promise()
        .then(r => {
            const rootId = r.items[0].id

            const params = {
                pathPart: path,
                parentId: rootId,
                restApiId: info.api_id
            }

            return apigateway.createResource(params).promise()
                            .then(c =>
                                Object.assign({
                                    resource_root_id: rootId,
                                    resource_id: c.id
                                }, info)
                            )
        })
}

const putMethod = info => {
    const params = {
        authorizationType: 'None',
        httpMethod: 'ANY',
        resourceId: info.resource_id,
        restApiId: info.api_id
    }

    return apigateway.putMethod(params).promise()
                    .then(dump)
                    .then(r => Object.assign({http_method: r.httpMethod}, info))
}

const putIntegration = funcArn => info => {
    const params = {
        httpMethod: info.http_method,
        resourceId: info.resource_id,
        restApiId: info.api_id,
        type: 'AWS_PROXY',
        integrationHttpMethod: 'POST',
        uri: `arn:aws:apigateway:${region}:lambda:path/2015-03-31/functions/${funcArn}/invocations`
    }

    return apigateway.putIntegration(params).promise()
                    .then(dump)
                    .then(r => Object.assign({uri: r.uri}, info))
}

const createDeployment = stage => info => {
    const params = {
        restApiId: info.api_id,
        stageName: stage
    }

    return apigateway.createDeployment(params).promise()
            .then(dump)
            .then(r => Object.assign({deployment_id: r.id}, info))
}

createApi(name)
    .then(createResource(path))
    .then(putMethod)
    .then(putIntegration(lambdaArn))
    .then(createDeployment(stage))
    .then(r => console.log(r))
    .catch(err => console.error(err))
