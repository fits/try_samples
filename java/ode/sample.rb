require 'soap/wsdlDriver'

wsdl = 'http://localhost:8080/ode/processes/sample?wsdl'

service = SOAP::WSDLDriverFactory.new(wsdl).create_rpc_driver
service.generate_explicit_type = true

req = {"input" => "aaa"}

puts service.hello(req)
