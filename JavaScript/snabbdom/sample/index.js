
import { init, h } from 'snabbdom';
import eventlisteners from 'snabbdom/modules/eventlisteners';

const patch = init([
	eventlisteners
]);

const container = document.getElementById('app');

const v1 = h('div');

patch(container, v1);

const clickHandle = (ev) => console.log(ev);

const v2 = h('div', [
	h('div', { on: { click: clickHandle }}, 'sample1'),
	h('div', 'sample2'),
	h('div', 'sample3')
]);

patch(v1, v2);
