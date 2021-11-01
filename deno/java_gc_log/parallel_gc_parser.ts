
const re = /([^ ]+): [^ ]+: (\[.+\]) (\[Times: .+\])/

export const parse = (lines: string[]) => {
    return lines.flatMap(line => {
        const m = re.exec(line)

        if (m) {
            return[{
                date: m[1],
                gc: parseGc(m[2]),
                time: parseTime(m[3])
            }]
        }

        return []
    })
}

const parseGc = (s: string) => {
    const ds = s.replace(/,/g, ']')
                .split('] ')
                .flatMap(t => t.split('['))
                .filter(t => t.trim().length > 0)

    const gens = Object.fromEntries(
        ds.slice(1, -1).map(d => {
            const p = d.split(': ')
            const scope = p.length > 1 ? p[0] : 'total'

            const m = /(.*)->(.*)\((.*)\)/.exec(p.slice(-1)[0]) ?? []

            return [
                scope, {
                    before: m[1], 
                    after: m[2], 
                    allocated: m[3]
                }
            ]
        })
    )

    const m = /([^()]+) \(([^)]+)\)/.exec(ds[0]) ?? []

    return Object.assign(
        {
            type: m[1],
            reason: m[2],
            time: ds.slice(-1)[0].split(' ')[0]
        },
        gens
    )
}

const parseTime = (s: string) => {
    const ds = s.split(' ').flatMap((t: string) => {
        if (t.includes('=')) {
            const [k, v] = t.split('=')
            return [[ k, v.replace(',', '') ]]
        }

        return []
    })

    return Object.fromEntries(ds)
}
