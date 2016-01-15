
var math = require('mathjs');

console.log(math.pow([[2, 3], [2, 1]], 2));

console.log(math.multiply([[2, 3], [2, 1]], [4, 7]));

console.log(math.det([[5, 4], [3, 2]]));

var m1 = math.matrix([[1, 2, 0, 0], [0, 0, 3, 4], [0, 5, 0, 6]], 'sparse');

console.log(m1);

console.log(math.trace([[5, 4], [3, 6]]));

console.log(math.trace([[5, 4, 1], [3, 6, 2], [10, 11, 12]]));

console.log(math.inv([[4, 1], [3, 2]]));

console.log(math.inv([[1, 3, -1], [0, 4, 0], [2, 3, 2]]));

console.log(math.transpose([[1, 3, -1], [0, 4, 2]]));
