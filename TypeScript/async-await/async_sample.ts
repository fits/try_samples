
const a = async () => 1
console.log(a())

const sample = (n: number) => Promise.resolve(n * 2)
const sample2 = async (n: number) => n * 2

const main = async () => {
	console.log( await sample(1) )
	console.log( await sample(2) )

	console.log( await sample2(3) )
	console.log( await sample2(4) )
}

main()
