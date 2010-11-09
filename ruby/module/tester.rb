
require "testModule.rb"

class Tester
	include TestModule

end

tester = Tester.new()
tester.printData()

