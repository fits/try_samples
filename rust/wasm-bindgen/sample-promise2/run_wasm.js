
const wasm = require('./pkg/sample_promise2.js')

global.sample = {
    initial_value: () => Promise.resolve({value: 'zero'})
}

const sample1 = async () => {
    const res = await wasm.sample(Promise.resolve({value: 'one'}))
    console.log(res)
}

const sample2 = async () => {
    const res = await wasm.sample(Promise.reject('test error'))
    console.log(res)
}

const run = async () => {
    await sample1()
    await sample2()
}

run().catch(err => console.error(err))
