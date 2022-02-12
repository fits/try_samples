import { pipeline } from 'stream/promises'
import { BatchTransform, FailedReadStream } from './streams'

pipeline(
    new FailedReadStream(10, 0.2),
    new BatchTransform(2),
    async (stream) => {
        for await (const rs of stream) {
            console.log(rs)
        }
    }
).catch(err => console.error(`stream error: ${err}`))
