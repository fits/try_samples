
const redis = require('redis')
const { promisify } = require('util')

const client = redis.createClient()

const get = promisify(client.get).bind(client)
const llen = promisify(client.llen).bind(client)
const lrange = promisify(client.lrange).bind(client)
const watch = promisify(client.watch).bind(client)

const key = process.argv[2]
const id = process.argv[3]

const dump = v => {
	console.log(v)
	return v
}

watch(key)
	.then( () => Promise.all([llen(key), get(`${key}.maxsize`)]) )
	.then(dump)
	.then( ([len, maxSize]) => {
		maxSize |= 0

		if (len >= parseInt(maxSize)) {
			throw new Error('no capacity')
		}

		return new Promise((resolve, reject) =>
			client.multi().lpush(key, id).exec((err, res) => {
				if (err) {
					reject(err)
				}
				else {
					resolve(res)
				}
			})
		)
	})
	.then(r => {
		console.log(r)
		return lrange(key, 0, -1)
	})
	.then(console.log)
	.catch(console.error)
	.finally(() => client.quit())
