import { SampleReadStream, BatchTransform } from './streams'

const stream = new SampleReadStream(7).pipe(new BatchTransform(3))

stream.on('data', d => {
    console.log(d)
})

stream.on('finish', () => {
    console.log('finish')
})
