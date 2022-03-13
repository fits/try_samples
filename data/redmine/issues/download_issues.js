
const n = parseInt(Deno.env.get('N') ?? '10')
const limit = parseInt(Deno.env.get('LIMIT') ?? '100')

const url = Deno.env.get('REDMINE_URL')
const apiKey = Deno.env.get('API_KEY')

let offset = parseInt(Deno.args[0] ?? '0')

const isObject = v => 
    v != null && typeof v === 'object' && !Array.isArray(v) 

const flatObj = obj => {
    const flatten = (d, prefix = '') => {
        return Object.entries(d).flatMap(([k, v]) => {
            let res = []

            if (isObject(v)) {
                res = res.concat(flatten(v, `${prefix}${k}.`))
            }
            else {
                res.push([`${prefix}${k}`, v])
            }

            return res
        })
    }

    return Object.fromEntries(flatten(obj))
}

try {
    for (let i = 0; i < n; i++) {
        const res = await fetch(`${url}/issues.json?status_id=*&sort=id&offset=${offset}&limit=${limit}`, {
            headers: {
                'X-Redmine-API-Key': apiKey
            }
        })

        const r = await res.json()

        r.issues.forEach(s => console.log(JSON.stringify(flatObj(s))))

        offset = r.offset + r.limit

        if (offset >= r.total_count) {
            break
        }
    }
} catch(err) {
    console.error(err)
}