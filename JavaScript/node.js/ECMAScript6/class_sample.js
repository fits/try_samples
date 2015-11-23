
"use strict";

class Data {
	constructor(name, value) {
		this.name = name;
		this.value = value;
	}

	static create(name) {
		return new Data(name, 1);
	}
}

let d1 = new Data('d1', 100);

console.log(d1);

console.log(Data.create('d2'));
