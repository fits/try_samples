
import { createStore } from 'redux';

const counter = (state = 0, act) => {
	switch (act.type) {
		case 'INC':
			return state + 1;
		case 'DEC':
			return state - 1;
		default:
			return state;
	}
};

const store = createStore(counter);

store.subscribe( () => console.log(store.getState()) );

store.dispatch({type: 'INC'});
store.dispatch({type: 'INC'});
store.dispatch({type: 'INC'});

store.dispatch({type: 'DEC'});
store.dispatch({type: 'DEC'});
