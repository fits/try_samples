require 'soap/wsdlDriver'

wsdl = 'SampleTestComponent.wsdl'

service = SOAP::WSDLDriverFactory.new(wsdl).create_rpc_driver
service.generate_explicit_type = true

request = {"input" => "aaa"}

puts service.hello(request)["SampleTestResponse"]

