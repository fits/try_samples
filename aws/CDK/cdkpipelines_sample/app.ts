
import { App, Stack, Construct, StackProps, Stage, StageProps } from '@aws-cdk/core'
import * as codepipeline from '@aws-cdk/aws-codepipeline'
import { CodeCommitSourceAction } from '@aws-cdk/aws-codepipeline-actions'
import * as codecommit from '@aws-cdk/aws-codecommit'
import { CdkPipeline, SimpleSynthAction } from '@aws-cdk/pipelines'

class SampleStack extends Stack {
    constructor(scope: Construct, id: string, props?: StackProps) {
        super(scope, id, props)

        // resource settings
    }
}

class SampleAppStage extends Stage {
    constructor(scope: Construct, id: string, props?: StageProps) {
        super(scope, id, props)

        new SampleStack(this, 'Sample')
    }
}

interface SamplePipelineProps extends StackProps {
    repoName: string
}

class SamplePipelineStack extends Stack {
    constructor(scope: Construct, id: string, props: SamplePipelineProps) {
        super(scope, id, props)

        const repo = codecommit.Repository.fromRepositoryName(
            this, 'Repo', props.repoName
        )

        const sourceArtifact = new codepipeline.Artifact()
        const cloudAssemblyArtifact = new codepipeline.Artifact()

        const pipeline = new CdkPipeline(this, 'Pipeline', {
            crossAccountKeys: false,
            cloudAssemblyArtifact,
            sourceAction: new CodeCommitSourceAction({
                actionName: 'CodeCommit',
                repository: repo,
                output: sourceArtifact
            }),
            synthAction: SimpleSynthAction.standardNpmSynth({
                sourceArtifact,
                cloudAssemblyArtifact
            })
        })

        pipeline.addApplicationStage(new SampleAppStage(this, 'Dev'))
    }
}

const app = new App()

new SamplePipelineStack(app, 'SamplePipelineStack', { repoName: 'sample' })

app.synth()
