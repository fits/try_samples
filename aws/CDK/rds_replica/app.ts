import { App, Stack } from '@aws-cdk/core'
import { StringParameter } from '@aws-cdk/aws-ssm'
import { DatabaseInstance, DatabaseInstanceReadReplica } from '@aws-cdk/aws-rds'
import * as ec2 from '@aws-cdk/aws-ec2'

const rdsIdParam = '/sample/id'
const rdsEndpointParam = '/sample/endpoint'
const rdsVpcParam = '/sample/vpcId'
const rdsSgParam = '/sample/securityGroupId'
const rdsPort = 3306

const app = new App()

const stack = new Stack(app, 'ReplicaSample', {
    env: {
        account: process.env.CDK_DEFAULT_ACCOUNT,
        region: process.env.CDK_DEFAULT_REGION
    }
})

const rdsId = StringParameter.valueFromLookup(stack, rdsIdParam)
const rdsEndpoint = StringParameter.valueFromLookup(stack, rdsEndpointParam)
const rdsVpcId = StringParameter.valueFromLookup(stack, rdsVpcParam)
const rdsSgId = StringParameter.valueFromLookup(stack, rdsSgParam)

const sg = ec2.SecurityGroup.fromSecurityGroupId(stack, 'RdsSecurityGroup', rdsSgId)

const db = DatabaseInstance.fromDatabaseInstanceAttributes(stack, 'DB', {
    instanceIdentifier: rdsId,
    instanceEndpointAddress: rdsEndpoint,
    port: rdsPort,
    securityGroups: [ sg ]
})

const vpc = ec2.Vpc.fromLookup(stack, 'RdsVpc', {
    vpcId: rdsVpcId
})

new DatabaseInstanceReadReplica(stack, 'Replica', {
    sourceDatabaseInstance: db,
    instanceType: ec2.InstanceType.of(
        ec2.InstanceClass.T2, 
        ec2.InstanceSize.MICRO
    ),
    vpc,
    vpcSubnets: vpc.selectSubnets()
})
