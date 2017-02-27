
import xs from 'xstream';
import { run } from '@cycle/xstream-run';
import { makeDOMDriver, div, button, input, p } from '@cycle/dom';
import { makeHTTPDriver } from '@cycle/http';

const main = (sources) => {
	const action$ = xs.merge(
		sources.DOM.select('.search').events('click'),
		sources.DOM.select('.word').events('keydown')
					.filter(ev => ev.which === 13)
	);

	const word$ = sources.DOM.select('.word').elements()
		.take(1)
		.map(el => el[0].value);

	const search$ = action$
		.map(() => word$)
		.flatten()
		.map(w => {
			return {
				url: 'http://localhost:9200/sample/_search',
				category: 'sample',
				method: 'POST',
				send: {
					query: {
						term: {
							_all: w
						}
					}
				}
			};
		});

	const searchResult$ = sources.HTTP.select('sample')
		.flatten()
		.map(res => res.body.hits.hits)
		.replaceError(err => {
			console.log(err);
			return xs.of([]);
		})
		.startWith([]);

	const vdom$ = searchResult$.map(xs => 
		div([
			input('.word', { attrs: { type: 'text' }}),
			button('.search', 'search'),
			p('Result:'),
			div(
				xs.map(x => div(`${x._id}, ${JSON.stringify(x._source)}`))
			)
		])
	);

	return {
		DOM: vdom$,
		HTTP: search$
	};
};

const drivers = {
	DOM: makeDOMDriver('#app'),
	HTTP: makeHTTPDriver()
};

run(main, drivers);
