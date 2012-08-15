
module Sample
	def sampleMethod(msg)
		"#{msg} !!!"
	end
end

class TestData
	attr_accessor :name, :point

	def initialize(name, point)
		@name = name
		@point = point
	end
end

d = TestData.new "test1", 10
puts "#{d.name}, #{d.point}"

# mixin Sample module
d.extend Sample
puts "#{d.name}, #{d.point}"

puts d.sampleMethod 'test'
