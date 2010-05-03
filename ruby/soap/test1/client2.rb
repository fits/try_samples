require 'defaultDriver.rb'

service = ServiceSoap.new('http://localhost:1099/SimpleTest/Service.asmx?wsdl')

param = CheckCount.new(1, 'aaaa')

puts service.checkCount(param).CheckCountResult

