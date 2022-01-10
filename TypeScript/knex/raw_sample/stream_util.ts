import { Transform, TransformCallback } from 'stream'

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

        this.batch.push(chunk)

        if (this.batch.length >= this.batchSize) {
            this.processBatch()
        }

        callback()
    }

    _flush(callback: TransformCallback): void {
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
