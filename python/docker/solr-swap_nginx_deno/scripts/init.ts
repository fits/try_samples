
const solrUrl = Deno.env.get('SOLR_URL') ?? ''

const maxRetry = parseInt(Deno.env.get('MAX_RETRY') ?? '10')
const sleepTime = parseInt(Deno.env.get('SLEEP_TIME') ?? '2')

const range = (n: number) => [...Array(n).keys()]

const sleep = (sec: number) => 
    new Promise((resolve) => setTimeout(resolve, sec * 1000))

const waitFor = async (url: string, sec: number, retry: number) => {
    for (let i = 0; i < retry; i++) {
        await sleep(sec)

        try {
            const res = await fetch(url, { method: 'HEAD' })

            if (res.ok) {
                return
            }
    
            console.log(`solr status: ${res.status}`)
        } catch(e) {
        }
    }

    console.log('timeout')
    Deno.exit(1)
}

await waitFor(`${solrUrl}/select`, sleepTime, maxRetry)

const docs = range(10).map(i => {
    return {
        name: `item-${i + 1}`,
        value: i,
        date: new Date().toISOString()
    }
})

const res = await fetch(`${solrUrl}/update?softCommit=true`, {
    method: 'POST',
    headers: {
        'Content-Type': 'application/json'
    },
    body: JSON.stringify(docs)
})

console.log(await res.text())

if (!res.ok) {
    Deno.exit(1)
}
