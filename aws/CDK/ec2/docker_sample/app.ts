
import { App, Stack, CfnOutput } from '@aws-cdk/core'
import * as ec2 from '@aws-cdk/aws-ec2'

const keyName = 'ec2-test'

const app = new App()

const stack = new Stack(app, 'EC2Sample', {
    env: {
        account: process.env.CDK_DEFAULT_ACCOUNT,
        region: process.env.CDK_DEFAULT_REGION
    }
})

const vpc = new ec2.Vpc(stack, 'Vpc', {
    cidr: '192.168.0.0/16',
    maxAzs: 2,
    natGateways: 0,
    subnetConfiguration: [
        { name: 'sample', subnetType: ec2.SubnetType.PUBLIC }
    ]
})

const ec2Group = new ec2.SecurityGroup(stack, 'EC2Connection', {
    vpc
})

ec2Group.addIngressRule(ec2.Peer.anyIpv4(), ec2.Port.tcp(22), 'allow ssh')

const ami = new ec2.AmazonLinuxImage({
    generation: ec2.AmazonLinuxGeneration.AMAZON_LINUX_2
})

const inst = new ec2.Instance(stack, 'EC2Instance', {
    vpc,
    securityGroup: ec2Group,
    instanceType: ec2.InstanceType.of(ec2.InstanceClass.T2, ec2.InstanceSize.MICRO),
    machineImage: ami,
    init: ec2.CloudFormationInit.fromConfig(new ec2.InitConfig([
        ec2.InitPackage.yum('docker'),
        ec2.InitCommand.shellCommand('service docker start'),
        ec2.InitCommand.shellCommand('usermod -a -G docker ec2-user')
    ])),
    keyName
})

new CfnOutput(stack, 'EC2 Endpoint', {
    value: inst.instancePublicDnsName
})
