
interface Data {
    name: string
    value: number
}

type DataKey = keyof Data

const k1: DataKey = 'name'
console.log(k1)

type Name = Data['name']

const n1: Name = 'a'
console.log(n1)

const prop = <T, K extends keyof T>(obj: T, key: K): T[K] => obj[key]

const d1: Data = { name: 'a1', value: 12 }

console.log(`${prop(d1, 'name')}, ${prop(d1, 'value')}`)

type ReadOnly<T> = {
    readonly [P in keyof T]: T[P]
}

type Stringify<T> = {
    [P in keyof T]: string
}

type ReadOnlyData = ReadOnly<Data>

const r1: ReadOnlyData = { name: 'r1', value: 1 }

type StringifyData = Stringify<Data>

const s1: StringifyData = { name: 's1', value: '1' }
