
var proc = function(p) {
	p.call();
};

var a = {
	a_id: '11',
	sample: function() {
		proc(() => {
			// this = a
			console.log(`id: ${this.a_id}`)
		});
	}
};

// id: 11
a.sample();

var a2 = {
	a_id: '22',
	sample: function() {
		proc(function() {
			// this = global object
			console.log(`id: ${this.a_id}`)
		});
	}
};

// id: undefined
a2.sample();