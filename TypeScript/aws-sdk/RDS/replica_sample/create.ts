import { RDSClient, CreateDBInstanceReadReplicaCommand } from '@aws-sdk/client-rds'

const dbId = process.argv[2]
const srcDbId = process.argv[3]

const client = new RDSClient({})

const cmd = new CreateDBInstanceReadReplicaCommand({
    DBInstanceIdentifier: dbId,
    SourceDBInstanceIdentifier: srcDbId
})

const run = async () => {
    const res = await client.send(cmd)
    console.log(res)
}

run().catch(err => console.error(err))
