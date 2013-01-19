var monadic = require('monadic');

var listM = monadic.list();

var res = listM.mbind([1, 2, 3], function(item) {
	return listM.mreturn(item * 10);
});

console.log(res);

var res2 = listM.mbind([1, 2, 3], function(item) {
	return [item * 10];
});

console.log(res2);
