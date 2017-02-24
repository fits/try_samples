
import m from 'mithril';

const Counter = {
	count: 0,
	view: (vnode) => m('div', [
		m('button', {onclick: () => vnode.state.count-- }, 'Down'),
		m('button', {onclick: () => vnode.state.count++ }, 'Up'),
		m('p', `Count: ${vnode.state.count}`)
	])
};

// 以下でも可
/*
const Counter = {
	oninit: (vnode) => {
		this.count = 0;
	},
	view: (vnode) => m('div', [
		m('button', {onclick: () => this.count-- }, 'Down'),
		m('button', {onclick: () => this.count++ }, 'Up'),
		m('p', `Count: ${this.count}`)
	])
};
*/

m.mount(document.getElementById('app'), Counter);
