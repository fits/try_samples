
import 'jsdom-global/register'

import { createElement as el } from '@bikeshaving/crank'
import { renderer } from '@bikeshaving/crank/dom'

renderer.render(el('div', {id: 'a1'}, 'abc123'), document.body)

console.log(document.body.innerHTML)
