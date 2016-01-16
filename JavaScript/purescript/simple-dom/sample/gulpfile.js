
var gulp = require('gulp');
var child_process = require('child_process');

var pulpCmd = (process.platform == 'win32')? 'pulp.cmd': 'pulp';
var destFile = 'sample.js'

gulp.task('pulp_package', () => {

	var res = child_process.spawnSync(pulpCmd, ['browserify', '--standalone', 'Sample', '-t', destFile]);

	[res.stdin, res.stdout, res.stderr].forEach( x => {
		if (x) {
			console.log(x.toString());
		}
	});
});

gulp.task('default', ['pulp_package']);
