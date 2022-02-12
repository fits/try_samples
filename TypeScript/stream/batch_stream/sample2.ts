import { BatchTransform, FailedReadStream } from './streams'

const stream = new FailedReadStream(10, 0.2)
    .on('error', err => {
        console.error(`read stream error: ${err}`)
    })
    .pipe(new BatchTransform(2))

stream.on('data', d => {
    console.log(d)
})

stream.on('finish', () => {
    console.log('finish')
})
