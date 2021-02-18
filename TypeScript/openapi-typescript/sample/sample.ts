
import { components, operations } from './api'

type ItemGet = operations['itemGet']
type Item = components['schemas']['Item']

const i1: Item = { name: 'item-1' }
const i2: Item = { name: 'item-2', related: i1 }

console.log(i1)
console.log(i2)

const r: ItemGet = {
    responses: {
        200: {
            content: { 'application/json': i2 }
        } 
    }
}

console.log(JSON.stringify(r))
