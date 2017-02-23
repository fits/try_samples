
import { fromEvent, fromPromise, of, merge } from 'most';
import Hogan from 'hogan.js';

const wordNode = document.getElementById('word');

const tpl = Hogan.compile(
	document.getElementById('tpl').innerHTML
);

const renderItems = rs => 
	document.getElementById('result').innerHTML = tpl.render({items: rs});

const searchParam = word => {
	return JSON.stringify({
		query: {
			term: {
				_all: word
			}
		}
	});
};

const search = word => 
	fromPromise(
		fetch('http://localhost:9200/sample/_search', {
			body: searchParam(word),
			method: 'POST'
		})
	)
	.flatMap(r => fromPromise(r.json()))
	.map(r => r.hits.hits)
	.recoverWith(err => of([]));

const toItem = rs => rs.map(r => {
	return { id: r._id, source: JSON.stringify(r._source) };
});

merge(
	fromEvent('click', document.getElementById('search')),
	fromEvent('keydown', wordNode).filter(ev => ev.which === 13)
)
.map(ev => wordNode.value)
.flatMap(search)
.map(toItem)
.observe(renderItems);
