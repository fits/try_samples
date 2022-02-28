
const normalize = v => {
    if (v == null) {
        return []
    }

    const vs = Array.isArray(v) ? v : [v]

    return vs.flatMap(v => v.toString().toLowerCase().split(' '))
}

export class SampleSearcher {
    fields = []
    index = {}
    docs = []

    constructor(fields = []) {
        this.fields = fields
    }

    store(doc) {
        const id = this.docs.length
        this.docs.push(doc)

        for (const f of this.fields) {
            if (doc.hasOwnProperty(f)) {
                if (!this.index[f]) {
                    this.index[f] = {}
                }

                for (const v of normalize(doc[f])) {
                    if (!this.index[f][v]) {
                        this.index[f][v] = []
                    }

                    this.index[f][v].push(id)
                }
            }
        }

        return id
    }

    search(queries) {
        const wrk = { index: null, field: null, queries: [] }

        for (const { field, query } of queries) {
            if (!field) {
                continue
            }

            const nq = query?.toLowerCase() ?? ''
            wrk.queries.push({ field, query: nq })

            if (this.fields.includes(field)) {
                const idx = this.index[field]?.[nq]

                if (idx?.length < (wrk.index?.length ?? this.docs.length)) {
                    wrk.index = idx
                    wrk.field = field
                }
            }
        }

        const filter = doc => {
            for (const { field, query } of wrk.queries) {
                if (field == wrk.field) {
                    continue
                }

                const ok = normalize(doc[field]).includes(query)

                if (!ok) {
                    return false
                }
            }
            return true
        }

        if (wrk.index) {
            const res = [] 

            for (const id of wrk.index) {
                const doc = this.docs[id]

                if (filter(doc)) {
                    res.push(doc)
                }
            }
    
            return res
        }

        return this.docs.filter(filter)
    }
}
