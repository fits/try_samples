var _ = require('underscore');
var plus = function (n) {
    return n + 3;
};
var times = function (n) {
    return n * 2;
};
var f1 = _.compose(times, plus);
var f2 = _.compose(plus, times);
// times(plus(4)) = 14
console.log(f1(4));
// plus(times(4)) = 11
console.log(f2(4));//@ sourceMappingURL=sample.js.map
