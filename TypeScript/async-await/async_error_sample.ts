
const sample = async (s: string) => s.length

const main1 = async () => {
	console.log( await sample('abc') )
	console.log( await sample('') )

	try {
		console.log( await sample(null) )
	} catch(e) {
		console.log(`*** ERROR1: ${e}`)
	}
}

const main2 = async () => {
	console.log( await sample(null).catch(e => `### ERROR2: ${e}`) )
	console.log( await sample('abcdef') )
}

main1()
main2()
