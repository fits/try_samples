
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

const d = {
    id: 12,
    project: {
        id: 1,
        name: 'sample'
    },
    labels: ['A1', 'B2'],
    description: null,
    enable: true,
    milestone: {
        1: {
            id: 11,
            created_at: '2022-03-06T21:11:39.996Z',
            labels: ['d3', 'e4']
        },
        2: {
            id: 22,
            created_at: '2022-03-18T19:52:07.727Z'
        }
    }
}

console.log(flatObj(d))
