var plus = function (n) {
    return n + 3;
};
var times = function (n) {
    return n * 2;
};
var compose = function (f, g) {
    return function (x) {
        return f(g(x));
    };
};
var f1 = compose(times, plus);
var f2 = compose(plus, times);
// times(plus(4)) = 14
console.log(f1(4));
// plus(times(4)) = 11
console.log(f2(4));//@ sourceMappingURL=sample2.js.map
