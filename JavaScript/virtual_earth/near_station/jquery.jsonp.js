// jQuery JSONP plugin
// version 1.1
// created by:
// Toshimasa Ishibashi
// iandeth99@ybb.ne.jp
// http://iandeth.dyndns.org/mt/ian/archives/000654.html

// Changes
// 2007-08-26 v1.1
//   * rewritten the script by wrapping with jQuery's $.extend()
// 2007-08-12 v1.0
//   * initial release

// inspired by the original code:
// JSON for jQuery by Michael Geary
// http://mg.to/2006/01/25/json-for-jquery
//
// changes and modification I applied to the original code:
//   + changed API from $("div").json() to $.getJSONP()
//   + added jQuery.noConflict() compatibility
//   + added deletion of appended jsonp scripts after execution
//   + added counter-base anonymous function naming
//   + added script.charset = 'utf-8'
//   + redefined name() and load() as $.json class methods
//   + deleted $.json.callbacks reservoir hash since it wasn't used
// thank you Michael, for sharing your code with us.

(function($) {

	$.extend({
		_jsonp : {
			scripts: {},
			charset: 'utf-8',
			counter: 1,
			head: document.getElementsByTagName("head")[0],
			name: function( callback ) {
				var name = '_jsonp_' +  (new Date).getTime()
					+ '_' + this.counter;
				this.counter++;
				var cb = function( json ) {
					eval( 'delete ' + name );
					callback( json );
					$._jsonp.head.removeChild( $._jsonp.scripts[ name ] );
					delete $._jsonp.scripts[ name ];
				};
				eval( name + ' = cb' );
				return name;
			},
			load: function( url, name ) {
				var script = document.createElement( 'script' );
				script.type    = 'text/javascript';
				script.charset = this.charset;
				script.src     = url;
				this.head.appendChild( script );
				this.scripts[ name ] = script;
			}
		},

		getJSONP : function ( url, callback ){
			var name = $._jsonp.name( callback );
			var url = url.replace( /{callback}/, name );
			$._jsonp.load( url, name );
			return this;
		}
	});

})(jQuery);
