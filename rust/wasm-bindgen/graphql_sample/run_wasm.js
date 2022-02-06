const wasm = require('./pkg/graphql_sample.js')

const ctx = new wasm.Context()

console.log(
    ctx.query(`
        mutation {
            create(input: { id: "item-1", value: 12 }) {
                id
            }
        }
    `)
)

console.log(
    ctx.query(`
        mutation {
            create(input: { id: "item-2", value: 34 }) {
                id
            }
        }
    `)
)

console.log(
    ctx.query(`
        query {
            find(id: "item-1") {
                id
                value
            }
        }
    `)
)

console.log(
    ctx.query(`
        {
            find(id: "item-2") {
                id
                value
            }
        }
    `)
)

console.log(
    ctx.query(`
        {
            find(id: "item-12") {
                id
            }
        }
    `)
)
