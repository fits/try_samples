class TestData
	def initialize(name)
		@name = name
	end
end

d1 = TestData.new "d1"
d2 = TestData.new "d2"
d3 = TestData.new "d3"

ObjectSpace.each_object(TestData) do |obj|
	p obj
end
