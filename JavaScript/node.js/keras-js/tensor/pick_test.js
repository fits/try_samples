const KerasJS = require('keras-js')

const d = new KerasJS.Tensor([1, 2, 3, 4, 5, 6], [3, 2])

console.log(d)

console.log( d.tensor.pick(0) )
console.log( d.tensor.pick(2) )

d2 = d.tensor.pick(1)

console.log( d2.get(0) )
console.log( d2.get(1) )
console.log( d2.get(2) )

console.log(d.tensor.pick(2, 1))
console.log(d.tensor.pick(2).pick(1))
