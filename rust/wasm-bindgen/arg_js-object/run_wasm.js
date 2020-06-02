
const wasm = require('./pkg/arg_js_obj.js')

class Data {
    constructor(name, value) {
        this.name = name
        this.value = value
    }

    show() {
        console.log(`Data name: ${this.name}, value: ${this.value}`)
    }
}

const data = new Data('data1', 11)

wasm.run(data)

wasm.run({
    name: 'data2', 
    value: 22, 
    show: () => console.log(`Data2`)
})
