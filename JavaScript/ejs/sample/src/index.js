
import 'ejs/ejs.min.js'

const ct = document.getElementById('tpl').innerHTML

const data = {
    items: {
        'A1': {
            'item1': 1,
            'item2': 2,
            'item3': 3
        },
        'B2': {
            'item4': 4
        },
        'C3': {
            'item5': 5,
            'item6': 6
        }
    }
}

document.getElementById('content').innerHTML = ejs.render(ct, data)
