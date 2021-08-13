
import { App, Duration, Stack, RemovalPolicy } from '@aws-cdk/core'
import { Runtime } from '@aws-cdk/aws-lambda'
import { NodejsFunction } from '@aws-cdk/aws-lambda-nodejs'
import { Secret } from '@aws-cdk/aws-secretsmanager'
import * as ec2 from '@aws-cdk/aws-ec2'
import * as rds from '@aws-cdk/aws-rds'

const dbUser = 'sysuser'
const secretName = 'lambda-rds-isolated-sample'

const app = new App()

const stack = new Stack(app, 'LambdaRdsIsolatedSample', {
    env: {
        account: process.env.CDK_DEFAULT_ACCOUNT,
        region: process.env.CDK_DEFAULT_REGION
    }
})

const vpc = new ec2.Vpc(stack, 'Vpc', {
    subnetConfiguration: [
        {
            name: 'rds',
            subnetType: ec2.SubnetType.ISOLATED
        }
    ]
})

const rdsGroup = new ec2.SecurityGroup(stack, 'RdsConnection', {
    vpc
})

const lambdaGroup = new ec2.SecurityGroup(stack, 'LambdaToRds', {
    vpc
})

rdsGroup.addIngressRule(lambdaGroup, ec2.Port.tcp(3306), 'allow lambda')

const dbCred = new Secret(stack, 'RdsCredentials', {
    secretName,
    generateSecretString: {
        secretStringTemplate: JSON.stringify({
            username: dbUser
        }),
        excludePunctuation: true,
        includeSpace: false,
        generateStringKey: 'password'
    }
})

const rdsInstance = new rds.DatabaseInstance(stack, 'RdsInstance', {
    engine: rds.DatabaseInstanceEngine.mysql({
        version: rds.MysqlEngineVersion.VER_8_0_25
    }),
    credentials: rds.Credentials.fromSecret(dbCred),
    vpc,
    vpcSubnets: {
        subnetType: ec2.SubnetType.ISOLATED
    },
    securityGroups: [ rdsGroup ],
    instanceType: ec2.InstanceType.of(ec2.InstanceClass.T2, ec2.InstanceSize.MICRO),
    removalPolicy: RemovalPolicy.DESTROY,
    deletionProtection: false
})

vpc.addInterfaceEndpoint('SecretsManagerEndpoint', {
    service: ec2.InterfaceVpcEndpointAwsService.SECRETS_MANAGER
})

const func = new NodejsFunction(stack, 'SearchRds', {
    runtime: Runtime.NODEJS_14_X,
    entry: 'src/index.ts',
    vpc,
    securityGroups: [ lambdaGroup ],
    environment: {
        RDS_ENDPOINT: rdsInstance.dbInstanceEndpointAddress,
        RDS_SECRET_NAME: secretName
    },
    timeout: Duration.seconds(10)
})

dbCred.grantRead(func)
