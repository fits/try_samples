
import { CloudFormationStackArtifact } from '@aws-cdk/cx-api'

import { 
    CloudFormationClient, 
    CreateChangeSetCommand, ExecuteChangeSetCommand, 
    DescribeChangeSetCommand, DescribeStacksCommand,
    Change, Stack
} from '@aws-sdk/client-cloudformation'

const MAX_RETRY = 100
const WAIT_TIME_CREATE = 5000
const WAIT_TIME_EXECUTE = 10000

const sleep = (ms: number) =>
    new Promise(resolve => setTimeout(resolve, ms))

const cf = new CloudFormationClient({})

type ChangeSetType = 'CREATE' | 'UPDATE'

export class CfDeployer {
    static async deploy(stackArtifact: CloudFormationStackArtifact): Promise<Stack | undefined> {
        const stackName = stackArtifact.stackName
        const changeSetType = await CfDeployer.selectChangeSetType(stackName)

        const r1 = await cf.send(new CreateChangeSetCommand({
            ChangeSetName: `ChangeSet-${Date.now()}`,
            StackName: stackName,
            TemplateBody: JSON.stringify(stackArtifact.template),
            ChangeSetType: changeSetType
        }))

        console.log(r1)

        const changeSetId = r1.Id

        if (!changeSetId) {
            throw new Error(`failed create ChangeSet: StackName=${stackName}`)
        }

        const cs = await CfDeployer.waitForCreate(changeSetId, stackName)

        if (cs.length < 1) {
            console.log('*** NO CHANGE')
            return CfDeployer.getStack(stackName)
        }

        const r2 = await cf.send(new ExecuteChangeSetCommand({
            ChangeSetName: changeSetId,
            StackName: stackName
        }))

        console.log(r2)

        return CfDeployer.waitForExecute(stackName)
    }

    static async getStackStatus(stackName: string): Promise<string> {
        const stack = await CfDeployer.getStack(stackName)
        return stack?.StackStatus ?? 'NONE'
    }

    static async getStack(stackName: string): Promise<Stack | undefined> {
        try {
            const r = await cf.send(new DescribeStacksCommand({
                StackName: stackName
            }))

            return r.Stacks?.[0]
        } catch(e) {
            if (e.Code !== 'ValidationError' || 
                e.message !== `Stack with id ${stackName} does not exist`) {
                throw e
            }
        }

        return undefined
    }

    private static async waitForCreate(changeSetId: string, stackName: string): Promise<Change[]> {

        for (let i = 0; i < MAX_RETRY; i++) {
            await sleep(WAIT_TIME_CREATE)

            const r = await cf.send(new DescribeChangeSetCommand({
                ChangeSetName: changeSetId,
                StackName: stackName
            }))

            if (r.Status === 'CREATE_PENDING' || 
                r.Status === 'CREATE_IN_PROGRESS') {

                continue
            }

            if (r.Status == 'CREATE_COMPLETE') {
                return r.Changes ?? []
            }

            if (r.Status == 'FAILED') {
                console.log(`*** failed: ${JSON.stringify(r)}`)
                return []
            }

            throw new Error(`failed create ChangeSet: ChangeSetId=${changeSetId}, StackName=${stackName}`)
        }

        throw new Error(`create ChangeSet timeout: ChangeSetId=${changeSetId}, StackName=${stackName}`)
    }

    private static async waitForExecute(stackName: string): Promise<Stack> {
        for (let i = 0; i < MAX_RETRY; i++) {
            await sleep(WAIT_TIME_EXECUTE)

            const stack = await CfDeployer.getStack(stackName)

            if (!stack) {
                throw new Error('not found')
            }

            if (!stack.StackStatus?.endsWith('_IN_PROGRESS')) {
                return stack
            }
        }

        throw new Error(`execute ChangeSet timeout: StackName=${stackName}`)
    }

    private static async selectChangeSetType(stackName: string): Promise<ChangeSetType> {

        const status = await CfDeployer.getStackStatus(stackName)

        return (status == 'NONE' || status == 'REVIEW_IN_PROGRESS') ? 
            'CREATE' : 'UPDATE'
    }
}
