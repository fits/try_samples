
import { App, Stack, Construct, StackProps, Duration } from '@aws-cdk/core'
import * as codepipeline from '@aws-cdk/aws-codepipeline'
import * as codebuild from '@aws-cdk/aws-codebuild'
import { CodeBuildAction } from '@aws-cdk/aws-codepipeline-actions'
import { CfnConnection } from '@aws-cdk/aws-codestarconnections'

import { GitHub2SourceAction } from './github2-source-action'

interface PipelineStackProps extends StackProps {
    readonly connectionName: string
    readonly repository: string
}

class PipelineStack extends Stack {
    constructor(scope: Construct, id: string, props: PipelineStackProps) {
        super(scope, id, props)

        const con = new CfnConnection(this, 'Connection', {
            connectionName: props.connectionName,
            providerType: 'GitHub'
        })

        const buildSpec = codebuild.BuildSpec.fromObject({
            version: '0.2',
            phases: {
                build: {
                    commands: [
                        'cat README.md'
                    ]
                }
            }
        })

        const sourceArtifact = new codepipeline.Artifact()
        const cloudAssemblyArtifact = new codepipeline.Artifact()

        const project = new codebuild.PipelineProject(this, 'Project', {
            buildSpec,
            timeout: Duration.minutes(5),
            environment: {
                buildImage: codebuild.LinuxBuildImage.STANDARD_5_0
            }
        })

        new codepipeline.Pipeline(this, 'Pipeline', {
            crossAccountKeys: false,
            stages: [
                {
                    stageName: 'Source',
                    actions: [
                        new GitHub2SourceAction({
                            actionName: 'GitHubSource',
                            connectionArn: con.attrConnectionArn,
                            repo: props.repository,
                            output: sourceArtifact
                        })
                    ]
                },
                {
                    stageName: 'Build',
                    actions: [
                        new CodeBuildAction({
                            actionName: 'CodeBuild',
                            project,
                            input: sourceArtifact,
                            outputs: [ cloudAssemblyArtifact ]
                        })
                    ]
                }
            ]
        })
    }
}

const app = new App()

new PipelineStack(app, 'GitHub2PipelineStack', {
    env: { region: process.env.CDK_DEFAULT_REGION },
    repository: 'user/repo-name',
    connectionName: 'github-connect1'
})
