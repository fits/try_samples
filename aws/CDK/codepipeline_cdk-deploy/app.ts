
import { App, Stack, Construct, StackProps, Duration } from '@aws-cdk/core'
import { ManagedPolicy } from '@aws-cdk/aws-iam'
import * as codebuild from '@aws-cdk/aws-codebuild'
import * as codecommit from '@aws-cdk/aws-codecommit'
import * as codepipeline from '@aws-cdk/aws-codepipeline'
import { CodeCommitSourceAction, CodeBuildAction } from '@aws-cdk/aws-codepipeline-actions'

interface PipelineStackProps extends StackProps {
    repositoryName: string
}

class PipelineStack extends Stack {
    constructor(scope: Construct, id: string, props: PipelineStackProps) {
        super(scope, id, props)

        const repo = codecommit.Repository.fromRepositoryName(
            this, 'Repo', props.repositoryName
        )

        const buildSpec = codebuild.BuildSpec.fromObject({
            version: '0.2',
            phases: {
                pre_build: {
                    commands: [ 'npm ci' ]
                },
                build: {
                    commands: [
                        'npx cdk synth',
                        'npx cdk deploy --all --require-approval never'
                    ]
                }
            }
        })

        const sourceArtifact = new codepipeline.Artifact()
        const cloudAssemblyArtifact = new codepipeline.Artifact()

        const project = new codebuild.PipelineProject(this, 'Project', {
            buildSpec,
            timeout: Duration.minutes(10),
            environment: {
                buildImage: codebuild.LinuxBuildImage.STANDARD_5_0
            }
        })

        const adminPolicy = ManagedPolicy.fromAwsManagedPolicyName('AdministratorAccess')

        project.role?.addManagedPolicy(adminPolicy)

        new codepipeline.Pipeline(this, 'Pipeline', {
            crossAccountKeys: false,
            stages: [
                {
                    stageName: 'Source',
                    actions: [
                        new CodeCommitSourceAction({
                            actionName: 'CodeCommit',
                            repository: repo,
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

new PipelineStack(app, 'PipelineStack', {
    env: { region: process.env.CDK_DEFAULT_REGION },
    repositoryName: 'sample'
})
