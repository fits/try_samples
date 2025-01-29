import { Kafka, AssignerProtocol } from 'kafkajs'

const clientId = process.env.KAFKA_CLIENT_ID ?? 'admin1'
const broker = process.env.KAFKA_BROKER ?? 'localhost:9092'

const kafka = new Kafka({
    clientId,
    brokers: [ broker ],
})

const admin = kafka.admin()

await admin.connect()

try {
    const topics = (await admin.listTopics()).filter(x => x != '__consumer_offsets')

    const groups = await admin.listGroups()

    const groupIds = groups.groups.map(x => x.groupId)

    const dgs = await admin.describeGroups(groupIds)

    for (const g of dgs.groups) {
        console.log(`* consumerGroup: groupId=${g.groupId}, state=${g.state}`)

        console.log(`  * members:`)

        for (const m of g.members) {
            console.log(`    * memberId=${m.memberId}, clientHost=${m.clientHost}`)

            const assign = AssignerProtocol.MemberAssignment.decode(m.memberAssignment)

            for (const [k, v] of Object.entries(assign.assignment)) {
                console.log(`      * assign: topic=${k}, partitions=${v}`)
            }
        }

        console.log(`  * topics:`)

        const ofs = await admin.fetchOffsets({
            groupId: g.groupId,
            topics
        })

        for (const o of ofs) {
            console.log(`    * topic=${o.topic}`)

            const tofs = await admin.fetchTopicOffsets(o.topic)

            for (const p of o.partitions) {
                const high = tofs.find(x => x.partition == p.partition)?.high
                console.log(`      * partition=${p.partition}, offset=${p.offset}, high=${high}, diff=${high - Math.max(0, p.offset)}`)
            }
        }
    }

} finally {
    await admin.disconnect()
}
