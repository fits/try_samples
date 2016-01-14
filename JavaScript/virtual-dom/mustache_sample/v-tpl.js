
var Promise = require('bluebird');
var Mustache = require('mustache');

var virtual = Promise.promisify(require('virtual-html'));

var diff = require('virtual-dom/diff');
var patch = require('virtual-dom/patch');

module.exports = node => {
	var tpl = node.outerHTML;
	var tree = null;

	return params => {
		var src = (tree)? Promise.resolve(tree): virtual(tpl);

		Promise.all([
			src,
			virtual(Mustache.render(tpl, params))
		]).spread( (r1, r2) => {
			patch(node, diff(r1, r2));
			tree = r2;
		});
	};
};
