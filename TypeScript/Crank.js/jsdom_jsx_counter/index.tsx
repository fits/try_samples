
/** @jsx createElement */
import { createElement } from '@bikeshaving/crank'
import { renderer } from '@bikeshaving/crank/dom'

import 'jsdom-global/register'

const printHtml = () => console.log(document.body.innerHTML)

function *Counter() {
    let count = 0

    setInterval(() => {
        count++
        this.refresh()
    }, 1000)

    while (true) {
        yield <div id="ct">count: {count}</div>
    }
}

renderer.render(<Counter />, document.body)

printHtml()
setInterval(printHtml, 1000)
