var coffee = require('coffee-script');

var code = 'a = x -> console.log x';
//var opt = { bare: true };
var opt = {};

var res = coffee.compile(code, opt);

console.log(res);
