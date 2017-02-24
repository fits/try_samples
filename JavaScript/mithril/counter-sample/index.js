
import m from 'mithril';

let count = 0;

const Counter = {
	view: () => m('div', [
		m('button', {onclick: () => count-- }, 'Down'),
		m('button', {onclick: () => count++ }, 'Up'),
		m('p', `Count: ${count}`)
	])
};

m.mount(document.getElementById('app'), Counter);
