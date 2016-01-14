
var h = require('hyperscript');

var doc = h('div#a', { name: 'sample1' }, 
	'sample',
	h('br'),
	h('ul', 
		h('li', h('a', { href: 'item1.html'}, 'item1')),
		h('li', h('a', { href: 'item2.html'}, 'item2'))
	)
);

console.log(doc);

console.log('---');

console.log(doc.outerHTML);
