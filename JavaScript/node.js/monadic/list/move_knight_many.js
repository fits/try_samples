var monadic = require('monadic');

var listM = monadic.list();

var moveKnight = function(p) {
	return [
		{c: p.c + 2, r: p.r - 1}, {c: p.c + 2, r: p.r + 1},
		{c: p.c - 2, r: p.r - 1}, {c: p.c - 2, r: p.r + 1},
		{c: p.c + 1, r: p.r - 2}, {c: p.c + 1, r: p.r + 2},
		{c: p.c - 1, r: p.r - 2}, {c: p.c - 1, r: p.r + 2}
	].filter(function(t) {
		return (1 <= t.c && t.c <= 8 && 1 <= t.r && t.r <= 8);
	});
};

var inMany = function(num, start) {
	var items = new Array(num);
	for (var i = 0; i < num; i++) {
		items.push(moveKnight);
	}

	return items.reduceRight(
		function(acc, elem) {
			return listM.mbind(acc, elem);
		}, 
		[start]
	);
};

console.log(inMany(3, {c: 6, r: 2}));

var canReachInMany = function(num, start, end) {
	return inMany(num, start).some(function(p) {
		return (p.c == end.c && p.r == end.r);
	});
};

console.log(canReachInMany(3, {c: 6, r: 2}, {c: 6, r: 1}));
console.log(canReachInMany(3, {c: 6, r: 2}, {c: 7, r: 3}));
