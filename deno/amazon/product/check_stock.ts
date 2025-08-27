const userAgent = 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/139.0.0.0 Safari/537.36'

const asin = Deno.args[0]
const n = parseInt(Deno.args[1])
const interval = parseInt(Deno.args[2]) * 60 * 1000

const url = `https://www.amazon.co.jp/dp/${asin}?m=AN1VRQENFRJN5`

const sleep = (time: number) => new Promise(resolve => setTimeout(resolve, time))

type F = (b: string) => boolean;

const checkPage = async (u: string, pred: F) => {
    const res = await fetch(u, { headers: { 'User-Agent': userAgent } })

    const body = await res.text()

    return pred(body)
}

const checkStock = (body: string) => body.includes('submit.add-to-cart')

const inStockAction = () => {
    console.log(`*** IN STOCK: ${new Date().toISOString()}`)
}

for (let i = 0; i < n; i++) {
    if (i > 0) {
        console.log('waiting for next check')
        await sleep(interval)
    }

    const result = await checkPage(url, checkStock)

    if (result) {
        inStockAction()
        break
    }
}
