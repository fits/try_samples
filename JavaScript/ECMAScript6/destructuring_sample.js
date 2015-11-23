
var [a, b, ] = [1, 2, 3, 4];

//a=1, b=2
console.log(`a=${a}, b=${b}`);

function sample({x, y}) {
	console.log(`x=${x}, y=${y}`);
}

// x=10, y=20
sample({x: 10, y: 20});
sample({x: 5});
sample({});
