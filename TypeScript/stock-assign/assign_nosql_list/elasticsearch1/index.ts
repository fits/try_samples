import { Client } from '@elastic/elasticsearch'

const host = process.env.ES_HOST ?? 'localhost'
const port = 9200

const endpoint = `http://${host}:${port}`
const index = 'stocks'

const client = new Client({ node: endpoint })

export const init = async (id, qty) => {
    await client.index({
        index,
        id,
        body: {
            qty,
            assigns: []
        },
        refresh: true
    })
}

export const assign = async (id, assignId, q) => {
    const r = await client.get({
        index,
        id
    })

    const s = r.body._source.qty
    const a = r.body._source.assigns.length

    if (s - a < q) {
        throw new Error('assign failed')
    }

    await client.update({
        index,
        id,
        if_seq_no: r.body._seq_no,
        if_primary_term: r.body._primary_term,
        body: {
            script: {
                source: 'ctx._source.assigns.addAll(params.data)',
                lang: 'painless',
                params: {
                    data: Array(q).fill(assignId)
                }
            }
        }
    })
}
