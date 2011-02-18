
require "rubygems"
require "haml"

s = <<EOS
!!! 5
%html
	%head
		%title= title
	%body
		.content= content
		%input{:type => 'hidden', :name => 'aaa'}
EOS

eng = Haml::Engine.new(s)

puts eng.render(Object.new, {:title => "haml test", :content => 'ƒeƒXƒg'})
