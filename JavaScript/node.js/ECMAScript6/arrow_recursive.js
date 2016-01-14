
var func = n => (n <= 1)? n: n * func(n - 1);

console.log(func(5));
