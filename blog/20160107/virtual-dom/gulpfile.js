
var fs = require('fs');

var gulp = require('gulp');
var browserify = require('browserify');

var flatten = require('gulp-flatten');

gulp.task('js-copy', () => {
	gulp.src('bower_components/*/dist/*.js')
		.pipe(flatten())
		.pipe(gulp.dest('js'));
});

gulp.task('browserify', () => {

	browserify({
		require: 'dom-delegator',
		standalone: 'DOMDelegator'
	}).bundle().pipe(fs.createWriteStream('js/dom-delegator.js'));

});

gulp.task('default', ['js-copy', 'browserify']);
