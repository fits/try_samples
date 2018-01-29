
import 'ejs/ejs.min.js'

const ct = document.getElementById('tpl').innerHTML

const data = {title: 'sample'}

document.getElementById('content').innerHTML = ejs.render(ct, data)
