
const puppeteer = require('puppeteer')

const endpoint = process.env.ENDPOINT

const run = async () => {
    const browser = await puppeteer.launch()

    const page = await browser.newPage()
    await page.goto(endpoint)

    const ct = await page.$('p:nth-child(2)')

    const logCounter = async () => {
        const content = await ct.getProperty('textContent')
        console.log(await content.jsonValue())
    }

    await logCounter()

    const btn = await page.$('button')
    await btn.click()

    await logCounter()

    await page.screenshot({ path: 'screenshot.png' })

    await browser.close()
}

run().catch(err => console.error(err))
