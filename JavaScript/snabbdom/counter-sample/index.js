
import { init, h } from 'snabbdom';
import eventlisteners from 'snabbdom/modules/eventlisteners';

const patch = init([
	eventlisteners
]);

let current = document.getElementById('app');

const render = count => {
	current = patch(current, counter(count));
};

const counter = (count) => h('div', [
	h('button', { on: { click: () => render(count - 1) }}, 'Down'),
	h('button', { on: { click: () => render(count + 1) }}, 'Up'),
	h('p', `Count: ${count}`)
]);

render(0);
