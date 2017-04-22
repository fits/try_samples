
import m from 'mithril';

let items = [];
let word = '';

const doSearch = () => m.request({
	url: 'http://localhost:9200/sample/_search',
	method: 'POST',
	data: {
		query: {
			term: {
				_all: word
			}
		}
	}
})
.then(r => items = r.hits.hits);

const SearchResult = {
	view: (vnode) => 
		items.map(it => m('div', `${it._id}, ${JSON.stringify(it._source)}`))
};

const Search = {
	view: (vnode) => m('div', [
		m('input', {
			type: 'text',
			oninput: m.withAttr('value', v => word = v),
			onkeydown: (ev) => {
				if (ev.which === 13) {
					doSearch();
				}
			}
		}),
		m('button', { onclick: doSearch }, 'search'),
		m('p', 'Result:'),
		m(SearchResult)
	])
};

m.mount(document.getElementById('app'), Search);
