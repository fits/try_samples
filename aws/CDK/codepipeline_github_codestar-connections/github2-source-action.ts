
import { Construct } from '@aws-cdk/core'
import * as iam from '@aws-cdk/aws-iam'
import * as codepipeline from '@aws-cdk/aws-codepipeline'
import { Action } from '@aws-cdk/aws-codepipeline-actions'

export interface GitHub2SourceActionProps {
    readonly output: codepipeline.Artifact
    readonly actionName: string
    readonly connectionArn: string
    readonly repo: string
    readonly branch?: string
}

export class GitHub2SourceAction extends Action {
    private readonly props: GitHub2SourceActionProps

    constructor(props: GitHub2SourceActionProps) {
        super({
            actionName: props.actionName,
            category: codepipeline.ActionCategory.SOURCE,
            owner: 'AWS',
            provider: 'CodeStarSourceConnection',
            artifactBounds: {
                minInputs: 0,
                maxInputs: 0,
                minOutputs: 1,
                maxOutputs: 1
            },
            outputs: [ props.output ]
        })

        this.props = props
    }

    protected bound(scope: Construct, stage: codepipeline.IStage, options: codepipeline.ActionBindOptions): codepipeline.ActionConfig {

        options.role.addToPrincipalPolicy(new iam.PolicyStatement({
            actions: [ 'codestar-connections:UseConnection' ],
            resources: [ this.props.connectionArn ]
        }))

        options.bucket.grantReadWrite(options.role)
        options.bucket.grantPutAcl(options.role)

        const branch = this.props.branch ? this.props.branch : 'main'

        return {
            configuration: {
                BranchName: branch,
                ConnectionArn: this.props.connectionArn,
                FullRepositoryId: this.props.repo,
                OutputArtifactFormat: 'CODE_ZIP'
            }
        }
    }
}
