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

console.log(moveKnight({c: 3, r: 1}));

var in3 = function(start) {
	return listM.mbind(
		listM.mbind(
			listM.mbind(
				[start], 
				moveKnight
			),
			moveKnight
		),
		moveKnight
	);
};

console.log(in3({c: 6, r: 2}));

var canReachIn3 = function(start, end) {
	return in3(start).some(function(p) {
		return (p.c == end.c && p.r == end.r);
	});
};

console.log(canReachIn3({c: 6, r: 2}, {c: 6, r: 1}));
console.log(canReachIn3({c: 6, r: 2}, {c: 7, r: 3}));
