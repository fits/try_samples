const file = Deno.args[0]

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

const ds = JSON.parse(await Deno.readTextFile(file))

ds.forEach(d => {
    d.forEach(s => {
        const r = flatObj(s)
        console.log(JSON.stringify(r))
    })
})
