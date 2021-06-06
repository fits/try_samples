import { marshall, unmarshall } from '@aws-sdk/util-dynamodb'

const itemM = marshall({
    name: 'sample data',
    lines: [
        { id: 'line-1', value: 12 },
        { id: 'line-2', value: 34 }
    ]
})

console.log(JSON.stringify(itemM))

const itemU = unmarshall(itemM)

console.log(JSON.stringify(itemU))
