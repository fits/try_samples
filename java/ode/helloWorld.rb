#Apache ODE Sample HelloWorld2 のクライアントサンプル

require 'soap/wsdlDriver'

wsdl = 'http://localhost:8080/ode/processes/helloWorld?wsdl'

service = SOAP::WSDLDriverFactory.new(wsdl).create_rpc_driver
service.generate_explicit_type = true

request = {"TestPart" => "aaa"}

puts service.hello(request)["TestPart"]

