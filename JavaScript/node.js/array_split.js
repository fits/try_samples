"use strict";

const splitArray = (arr, size) => {
	const res = [];

	for (let i = 0; i < Math.ceil(arr.length / size); i++) {
		res.push(arr.slice(i * size, (i + 1) * size));
	}

	return res;
};

console.log(splitArray([], 2));

console.log(splitArray([1, 2, 3, 4, 5], 1));
console.log(splitArray([1, 2, 3, 4, 5], 2));
console.log(splitArray([1, 2, 3, 4, 5], 3));
console.log(splitArray([1, 2, 3, 4, 5], 4));
console.log(splitArray([1, 2, 3, 4, 5], 5));
console.log(splitArray([1, 2, 3, 4, 5], 6));

console.log(splitArray([1, 2, 3, 4, 5, 6], 2));
