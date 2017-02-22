
import xs from 'xstream';
import { run } from '@cycle/xstream-run';
import { makeDOMDriver, p } from '@cycle/dom';

const main = () => {
	const sinks = {
		DOM: xs.periodic(1000)
				.map(n => p(new Date().toLocaleTimeString()))
	};

	return sinks;
};

const drivers = {
	DOM: makeDOMDriver('#app')
};

run(main, drivers);
