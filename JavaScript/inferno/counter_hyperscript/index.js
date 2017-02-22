
import Inferno, { linkEvent } from 'inferno';
import h from 'inferno-hyperscript';

const handleClick = (newCount, event) => {
	console.log(newCount);
	console.log(event);

	render(newCount);
}

const Counter = count => 
	h('div', [
		h('button', { onClick: linkEvent(count - 1, handleClick) }, 'Down'),
		h('button', { onClick: linkEvent(count + 1, handleClick) }, 'Up'),
		h('p', `count: ${count}`)
	]);

const render = count => 
	Inferno.render(Counter(count), document.getElementById('app'));

render(0);
