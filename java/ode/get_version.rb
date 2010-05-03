require 'soap/wsdlDriver'

wsdl = 'http://localhost:8080/ode/processes/Version?wsdl'

service = SOAP::WSDLDriverFactory.new(wsdl).create_rpc_driver
service.generate_explicit_type = true

puts service.getVersion().return
