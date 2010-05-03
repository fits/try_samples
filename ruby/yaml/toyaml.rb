require 'jcode'
require 'yaml'

$KCODE='u'

class Test

	attr_accessor :name, :point

	def initialize(name, point)
		@name = name
		@point = point
	end

end

puts Test.new("test", 1).to_yaml
puts Test.new("てすと", 10).to_yaml
