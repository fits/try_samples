
require "rubygems"
require "haml"

eng = Haml::Engine.new(".test abc")

puts eng.render
#=> "<div class='test'>abc</div>"
