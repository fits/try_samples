
require "rubygems"
require "haml"

class TestData
	def to_list
		["a", "b", "c"]
	end
end

s = <<EOS
!!! 5
%html
	%head
		%title= title
	%body
		.content= content
		%input{:type => 'hidden', :name => 'aaa'}
		%ul
			- to_list.each do |l|
				%li.data= l
EOS

eng = Haml::Engine.new(s)

puts eng.render(TestData.new, {:title => "haml test", :content => 'ƒeƒXƒg'})
