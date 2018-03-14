
const wait = (ms: number) => new Promise(rs => setTimeout(rs, ms))

const main = async () => {
	console.log('start')

	await wait(1000)
	console.log('after 1s')

	await wait(2000)
	console.log('after 2s')

	await wait(3000)
	console.log('after 3s')
}

main()
