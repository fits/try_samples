const preprocess = require('svelte-preprocess');

/** @type {import('@sveltejs/kit').Config} */
module.exports = {
	preprocess: preprocess(),

	kit: {
		target: '#svelte'
	}
};
