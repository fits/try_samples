
const Studio = require('studio');

Studio(function sampleService(name) {
	return `sample : ${name}`;
});

const serviceRef = Studio('sampleService');

serviceRef('aaa').then( msg => console.log(msg) );
