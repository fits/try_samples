
var Promise = require('bluebird');
var parser = Promise.promisify(require('html2hscript'));

var html = '<div name="sample1" id="a">sample<br /><ul var="items" v-click="items_click"><li><a href="item1.html">item1</a></li><li><a href="item2.html">item2</a></li></ul></div>';

parser(html).then( hs => console.log(hs) );
