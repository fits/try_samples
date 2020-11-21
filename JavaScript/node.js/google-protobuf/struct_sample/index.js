
const { Struct, Value } = require('google-protobuf/google/protobuf/struct_pb')

const testStruct = v => {
    console.log(v)

    const s = Struct.fromJavaScript(v)

    console.log(s)
    console.log(JSON.stringify(s.toObject()))

    console.log(s.toJavaScript())
}

const testValue = v => {
    console.log(v)

    const s = Value.fromJavaScript(v)

    console.log(s)
    console.log(JSON.stringify(s.toObject()))

    console.log(s.toJavaScript())
}

testStruct({name: 'item-1', value: 123})

console.log('-----')

testValue({name: 'item-1', value: 123})

console.log('-----')

testValue(123)

console.log('-----')

testValue(null)
