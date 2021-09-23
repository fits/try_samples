
import { App, Stack } from '@aws-cdk/core'
import { StringParameter } from '@aws-cdk/aws-ssm'
import * as ec2 from '@aws-cdk/aws-ec2'

const vpcIdParamName = '/sample/vpcid'

const app = new App()

const stack = new Stack(app, 'VPCPeeringSample', {
    env: {
        account: process.env.CDK_DEFAULT_ACCOUNT,
        region: process.env.CDK_DEFAULT_REGION
    }
})

const vpc = new ec2.Vpc(stack, 'Vpc', {
    cidr: '192.168.0.0/16',
    subnetConfiguration: [
        { name: 'sample', subnetType: ec2.SubnetType.PUBLIC }
    ]
})

const peerVpc = ec2.Vpc.fromLookup(stack, 'PeerVpc', {
    vpcId: StringParameter.valueFromLookup(stack, vpcIdParamName)
})

const vpcPeering = new ec2.CfnVPCPeeringConnection(stack, 'VpcPeering', {
    vpcId: vpc.vpcId,
    peerVpcId: peerVpc.vpcId
})

vpc.publicSubnets.map((s, i) => {
    new ec2.CfnRoute(stack, `src-peering-${i}`, {
        routeTableId: s.routeTable.routeTableId,
        destinationCidrBlock: peerVpc.vpcCidrBlock,
        vpcPeeringConnectionId: vpcPeering.ref
    })
})

peerVpc.isolatedSubnets.map((s, i) => {
    new ec2.CfnRoute(stack, `trg-peering-${i}`, {
        routeTableId: s.routeTable.routeTableId,
        destinationCidrBlock: vpc.vpcCidrBlock,
        vpcPeeringConnectionId: vpcPeering.ref
    })
})
