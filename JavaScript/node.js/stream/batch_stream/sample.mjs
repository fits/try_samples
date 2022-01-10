
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
        console.log('*** _transform')

        this.batch.push(chunk)

        if (this.batch.length >= this.batchSize) {
            this.processBatch()
        }

        callback()
    }

    _flush(callback) {
        console.log('*** _flush')

        if (this.batch.length > 0) {
            this.processBatch()
        }

        callback()
    }

    processBatch() {
        this.push(this.batch)
        this.batch = []
    }
}

const stream = new SampleReadStream(7).pipe(new SampleTransform(3))

stream.on('data', d => {
    console.log(d)
})

stream.on('finish', () => {
    console.log('finish')
})
