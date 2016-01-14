
var Promise = require('bluebird');
var virtual = Promise.promisify(require('virtual-html'));
var Mustache = require('mustache');

var diff = require('virtual-dom/diff');

var html = `<div id="a">
  {{title}}
  <br />
  <ul>
    {{#items}}
    <li>{{name}}</li>
    {{/items}}
  </ul>
</div>`;

var params = { title: 'sample1', items: [
	{name: 'item1'},
	{name: 'item2'}
]};

console.log(Mustache.render(html, params));

Promise.all([
	virtual(html),
	virtual(Mustache.render(html, params))
]).spread( (r1, r2) => {
	console.log('-----');

	console.log(r1);

	console.log('-----');

	console.log(r2);

	console.log('-----');

	console.log( diff(r1, r2) );
});
