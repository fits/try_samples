
run lambda {|env|
	puts env

	[200, {'Content-Type' => 'text/plain'}, ['sample page']]
}
