var monadic = require('monadic');

var listM = monadic.list();

var moveKnight = function(p) {
	return [
		{x: p.x + 2, y: p.y - 1}, {x: p.x + 2, y: p.y + 1},
		{x: p.x - 2, y: p.y - 1}, {x: p.x - 2, y: p.y + 1},
		{x: p.x + 1, y: p.y - 2}, {x: p.x + 1, y: p.y + 2},
		{x: p.x - 1, y: p.y - 2}, {x: p.x - 1, y: p.y + 2}
	].filter(function(t) {
		return (1 <= t.x && t.x <= 8 && 1 <= t.y && t.y <= 8);
	});
};

console.log(moveKnight({x: 3, y: 1}));

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

console.log(in3({x: 6, y: 2}));

var canReachIn3 = function(start, end) {
	return in3(start).some(function(p) {
		return (p.x == end.x && p.y == end.y);
	});
};

console.log(canReachIn3({x: 6, y: 2}, {x: 6, y: 1}));
console.log(canReachIn3({x: 6, y: 2}, {x: 7, y: 3}));
