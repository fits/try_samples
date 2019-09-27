
const merge = a => b => Object.fromEntries([a, b].map(Object.entries).flat())

console.log(
    merge({a: 1, b: 2})({c: 3, d: 4})
)

console.log(
    merge({a: 1, b: 2})({a: 3, d: 4})
)
