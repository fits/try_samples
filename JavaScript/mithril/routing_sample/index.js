
import m from 'mithril';

const Page1 = {
	view: () => m('p', 'PAGE1')
};

const Page2 = {
	view: () => m('div', 'page2')
};

const Menu = {
	view: () => m('div', [
		m('a', {href: '#!/page1'}, 'Page1'),
		m('a', {href: '#!/page2'}, 'Page2'),
	])
}

m.mount(document.getElementById('menu'), Menu);

m.route(document.getElementById('content'), '/page1', {
	'/page1': Page1,
	'/page2': Page2
});
