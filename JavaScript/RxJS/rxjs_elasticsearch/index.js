
import Rx from 'rxjs/Rx';
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
	Rx.Observable
		.ajax({
			url: 'http://localhost:9200/sample/_search', 
			body: searchParam(word),
			method: 'POST'
		})
		.map(r => r.response)
		.map(r => r.hits.hits)
		.catch(err => Rx.Observable.of([]));

const toItem = rs => rs.map(r => {
	return { id: r._id, source: JSON.stringify(r._source) };
});

Rx.Observable.fromEvent(document.getElementById('search'), 'click')
	.map(ev => wordNode.value)
	.flatMap(search)
	.map(toItem)
	.subscribe(renderItems);
