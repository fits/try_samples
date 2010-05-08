require "test/unit"
require "personaldata.rb"

class PersonalDataTest < Test::Unit::TestCase

	def setup()
		@data = PersonalData.new()
	end

	def test_check()
		assert_equal(true, @data.check())
	end

	def test_name()
		assert_nil(@data.name)
		
		@data.name = "test"
		
		assert_equal("test", @data.name)
	end

end
