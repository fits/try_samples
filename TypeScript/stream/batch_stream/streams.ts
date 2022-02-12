import { Readable, Transform, TransformCallback } from 'stream'

export class BatchTransform extends Transform {
    private batchSize: number
    private batch: any[]

    constructor(batchSize: number) {
        super({
            objectMode: true
        })

        this.batchSize = batchSize
        this.batch = []
    }

    _transform(chunk: any, _encoding: BufferEncoding, callback: TransformCallback): void {
        console.log('*** _transform')

        this.batch.push(chunk)

        if (this.batch.length >= this.batchSize) {
            this.processBatch()
        }

        callback()
    }

    _flush(callback: TransformCallback): void {
        console.log('*** _flush')

        if (this.batch.length > 0) {
            this.processBatch()
        }

        callback()
    }

    private processBatch() {
        this.push(this.batch)
        this.batch = []
    }
}

export class SampleReadStream extends Readable {
    constructor(readCount: number) {
        super({
            objectMode: true
        })

        for (let i = 0; i < readCount; i++) {
            this.push(`data-${i + 1}`)
        }

        this.push(null)
    }
}
