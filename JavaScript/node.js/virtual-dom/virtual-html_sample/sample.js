
var Promise = require('bluebird');
var virtual = Promise.promisify(require('virtual-html'));
var diff = require('virtual-dom/diff');

var html = `<div name="sample1" id="a">
  sample
  <br />
  <ul var="items" v-click="items_click">
    <li><a href="item1.html">item1</a></li>
    <li><a href="item2.html">item2</a></li>
  </ul>
</div>`;

virtual(html).then( r => {
	console.log(r);
	console.log('-------');

	console.log(r.children[3].properties);

	console.log('-------');
});

Promise.all([
	virtual('<div />'),
	virtual(html)
]).spread( (r1, r2) => {
	var d = diff(r1, r2);
	console.log(d);
});
