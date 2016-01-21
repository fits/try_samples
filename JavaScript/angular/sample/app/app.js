
(function(app) {

	document.addEventListener('DOMContentLoaded', function() {

		app.AppComponent = ng.core.Component({
			selector: 'my-app',
			template: '<h1>title: {{title}}</h1>'
		}).Class({
			constructor: function() {
				this.title = 'angular2 sample';
			}
		});

		ng.platform.browser.bootstrap(app.AppComponent);
	});

})(window.app || (window.app = {}));