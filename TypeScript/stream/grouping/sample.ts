import { Readable } from 'stream'
import { GroupingTransform } from './grouping'

class ArrayReadStream extends Readable {
    constructor(ds: any[]) {
        super({
            objectMode: true
        })

        ds.forEach(d => this.push(d))
        this.push(null)
    }
}

const data: any[] = [
    { id: 1, category: 'A' },
    { id: 2, category: 'B' },
    { id: 3, category: 'B' },
    { id: 4, category: 'C' },
    { id: 5, category: 'D' },
    { id: 6, category: 'D' },
    { id: 7, category: 'D' },
    { id: 8, category: 'A' },
    { id: 9, category: 'A' },
]

const stream = new ArrayReadStream(data).pipe(new GroupingTransform('category'))

stream.on('data', d => console.log(d))
stream.on('finish', () => console.log('*** finish'))
