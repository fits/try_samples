import { timeStamp } from 'console'
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

export class FailedReadStream extends Readable {
    private errorProb: number
    private maxSize: number
    private current: number

    constructor(maxSize: number, errorProb: number = 0.0) {
        super({
            objectMode: true
        })

        this.current = 0
        this.maxSize = maxSize
        this.errorProb = errorProb
    }

    _read(size: number): void {
        console.log(`*** read: size=${size}`)

        this.current++

        if (this.errorProb > Math.random()) {
            this.destroy(new Error('failed'))
            //this.emit('error', new Error('failed'))
            return
        }

        if (this.current <= this.maxSize) {
            this.push(`data-${this.current}`)
        }
        else {
            this.push(null)
        }



/*
        for (let i = 1; i <= size; i++) {
            if (this.current >= this.maxSize) {
                this.push(null)
                break
            }

            this.current += 1

            if (this.errorRate > Math.random()) {
                setTimeout(() => {
                    this.emit('error', new Error('failed'))
                }, 1)

                continue
            }

            this.push(`data-${this.current}`)
        }
        */
    }
}
