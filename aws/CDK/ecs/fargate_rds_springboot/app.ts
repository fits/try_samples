
import * as cdk from 'aws-cdk-lib'
import * as ec2  from 'aws-cdk-lib/aws-ec2'
import * as ecs from 'aws-cdk-lib/aws-ecs'
import { ApplicationLoadBalancedFargateService } from 'aws-cdk-lib/aws-ecs-patterns'
import { Repository } from 'aws-cdk-lib/aws-ecr'
import { StringParameter } from 'aws-cdk-lib/aws-ssm'
import { Secret } from 'aws-cdk-lib/aws-secretsmanager'

const rdsVpcIdParam = '/sample/rds/vpc-id'
const rdsSgIdParam = '/sample/rds/sg-id'
const rdsUrlParam = '/sample/rds/jdbc-url'

const secretName = '/sample/rds'

const imageName = 'sample'
const imageTag = '0.0.1-SNAPSHOT'

const app = new cdk.App()

const stack = new cdk.Stack(app, 'EcsSpringBootStack', {
    env: {
        account: process.env.CDK_DEFAULT_ACCOUNT,
        region: process.env.CDK_DEFAULT_REGION,
    }
})

const rdsVpcId = StringParameter.valueFromLookup(stack, rdsVpcIdParam)
const rdsSgId = StringParameter.valueFromLookup(stack, rdsSgIdParam)
const rdsUrl = StringParameter.valueFromLookup(stack, rdsUrlParam)

const vpc = new ec2.Vpc(stack, 'Vpc', {
    maxAzs: 2,
    cidr: '10.10.0.0/16'
})

const sg = new ec2.SecurityGroup(stack, 'SecurityGroup', {
    vpc
})

const rdsVpc = ec2.Vpc.fromLookup(stack, 'RdsVpc', {
    vpcId: rdsVpcId
})

const vpcPeering = new ec2.CfnVPCPeeringConnection(stack, 'VpcPeering', {
    vpcId: vpc.vpcId,
    peerVpcId: rdsVpc.vpcId
})

vpc.privateSubnets.map((s, i) => {
    new ec2.CfnRoute(stack, `src-peering-${i}`, {
        routeTableId: s.routeTable.routeTableId,
        destinationCidrBlock: rdsVpc.vpcCidrBlock,
        vpcPeeringConnectionId: vpcPeering.ref
    })
})

rdsVpc.isolatedSubnets.map((s, i) => {
    new ec2.CfnRoute(stack, `trg-peering-${i}`, {
        routeTableId: s.routeTable.routeTableId,
        destinationCidrBlock: vpc.vpcCidrBlock,
        vpcPeeringConnectionId: vpcPeering.ref
    })
})

ec2.SecurityGroup.fromSecurityGroupId(stack, 'RdsSecurityGroup', rdsSgId)
    .addIngressRule(sg, ec2.Port.tcp(3306), 'allow fargate service')

const rdsSecret = Secret.fromSecretNameV2(stack, 'RdsSecret', secretName)

const cluster = new ecs.Cluster(stack, 'Cluster', { vpc })

const fargate = new ApplicationLoadBalancedFargateService(stack, 'FargateService', {
    cluster,
    cpu: 1024,
    memoryLimitMiB: 2048,
    securityGroups: [ sg ],
    taskImageOptions: {
        image: ecs.ContainerImage.fromEcrRepository(
            Repository.fromRepositoryName(stack, 'Repository', imageName),
            imageTag
        ),
        containerPort: 8080,
        environment: {
            'SPRING_DATASOURCE_URL': rdsUrl
        },
        secrets: {
            'SPRING_DATASOURCE_USERNAME': ecs.Secret.fromSecretsManager(rdsSecret, 'username'),
            'SPRING_DATASOURCE_PASSWORD': ecs.Secret.fromSecretsManager(rdsSecret, 'password')
        }
    },
    healthCheckGracePeriod: cdk.Duration.minutes(3)
})

fargate.targetGroup.configureHealthCheck({
    path: '/actuator/health'
})

cdk.Tags.of(stack).add('type', 'sample')

app.synth()