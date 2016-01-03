
"use strict";

// module Sample.Math

exports.plus = function(a) {
	return function(b) {
		return a + b;
	}
};
