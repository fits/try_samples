import { Transform, TransformCallback } from 'stream'

export class GroupingTransform extends Transform {
    private fieldName: string
    private group: any[]
    private current: any

    constructor(fieldName: string) {
        super({
            objectMode: true
        })

        this.group = []
        this.fieldName = fieldName
    }

    _transform(chunk: any, _encoding: BufferEncoding, callback: TransformCallback): void {
        const v = chunk[this.fieldName]

        if (this.current !== v) {
            this.processGroup()
            this.current = v
        }

        this.group.push(chunk)

        callback()
    }

    _flush(callback: TransformCallback): void {
        this.processGroup()
        callback()
    }

    private processGroup() {
        if (this.group.length > 0) {
            this.push(this.group)
            this.group = []
        }
    }
}
