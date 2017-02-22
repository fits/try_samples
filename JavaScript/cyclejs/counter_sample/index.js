
import xs from 'xstream';
import { run } from '@cycle/xstream-run';
import { makeDOMDriver, div, button, p } from '@cycle/dom';

const main = (sources) => {
	const action$ = xs.merge(
		sources.DOM.select('.dec').events('click').mapTo(-1),
		sources.DOM.select('.inc').events('click').mapTo(+1)
	);

	const count$ = action$.fold((acc, v) => acc + v, 0);

	const vdom$ = count$.map(ct => div([
		button('.dec', 'Down'),
		button('.inc', 'Up'),
		p(`count: ${ct}`)
	]));

	return {
		DOM: vdom$
	};
};

const drivers = {
	DOM: makeDOMDriver('#app')
};

run(main, drivers);
