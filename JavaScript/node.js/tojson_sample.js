
class Data {
	constructor(id, name) {
		this.id = id
		this.name = name
	}
}

class Data2 {
	constructor(id, name) {
		this.id = id
		this.name = name
	}

	toJSON() {
		return { id: this.id, name: this.name, v: 1 }
	}

	static fromJSON(json) {
		return new Data2(json.id, json.name)
	}
}


const d1 = new Data('d1', 'data1')

console.log(d1)

const j1 = JSON.stringify(d1)

console.log(j1)

const r1 = JSON.parse(j1)

console.log(r1)

console.log(new Data(r1.id, r1.name))

console.log('------')

const d2 = new Data2('d2', 'data2')

console.log(d2)

const j2 = JSON.stringify(d2)

console.log(j2)

console.log(Data2.fromJSON(JSON.parse(j2)))
