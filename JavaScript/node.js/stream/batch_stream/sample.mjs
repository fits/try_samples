
import { Readable, Transform } from 'stream'

class SampleReadStream extends Readable {
    constructor(readCount) {
        super({
            objectMode: true
        })

        for (let i = 0; i < readCount; i++) {
            this.push(`data-${i + 1}`)
        }

        this.push(null)
    }
}

class SampleTransform extends Transform {
    constructor(batchSize) {
        super({
            objectMode: true
        })

        this.batchSize = batchSize
        this.batch = []
    }

    _transform(chunk, _encoding, callback) {
        console.log('*** transform')

        this.batch.push(chunk)

        if (this.batch.length >= this.batchSize) {
            this.push(this.batch)
            this.batch = []
        }

        callback()
    }

    _flush(callback) {
        console.log('*** _flash')

        if (this.batch.length > 0) {
            this.push(this.batch)
            this.batch = []
        }

        callback()
    }
}

const stream = new SampleReadStream(7).pipe(new SampleTransform(3))

stream.on('data', d => {
    console.log(d)
})

stream.on('finish', () => {
    console.log('finish')
})
