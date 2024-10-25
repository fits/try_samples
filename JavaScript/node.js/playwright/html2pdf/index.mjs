import { chromium } from 'playwright'
import fs from 'fs'

const browser = await chromium.launch()
const page = await browser.newPage()

await page.setContent(`
    <html>
    <body>
        <h1>sample page</h1>
        <hr />
        <ul>
            <li>item1</li>
            <li>item2</li>
            <li>item3</li>
        </ul>
    </body>
    </html>    
`)

const data = await page.pdf()

fs.writeFileSync('output.pdf', data)

await browser.close()
