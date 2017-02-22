
import Inferno from 'inferno';
import { periodic } from 'most';

const Time = () => Inferno.createVNode(2, 'p', {}, 
							new Date().toLocaleTimeString());

periodic(1000)
	.observe(v => Inferno.render(Time(), document.getElementById('app')));
