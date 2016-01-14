
var fs = require('fs');
var gulp = require('gulp');
var browserify = require('browserify');

gulp.task('browserify', () => {

	browserify('v-tpl.js', { standalone: 'VTpl' })
		.bundle()
		.pipe(fs.createWriteStream('www/v-tpl_dist.js'));

});

gulp.task('default', ['browserify']);
