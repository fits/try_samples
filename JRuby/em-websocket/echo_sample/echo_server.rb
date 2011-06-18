require 'rubygems'
require 'em-websocket'

EventMachine::WebSocket.start(:host => "localhost", :port => 8080, :debug => true) do |ws|

	ws.onopen {puts "onopen"}
	ws.onmessage {|msg| ws.send "echo : #{msg}"}
	ws.onclose {puts "onclose"}

end