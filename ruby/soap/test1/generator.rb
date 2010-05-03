require 'wsdl/soap/wsdl2ruby'

wsdl = 'http://localhost:1099/SimpleTest/Service.asmx?wsdl'

gen = WSDL::SOAP::WSDL2Ruby.new
gen.location = wsdl
gen.opt.update({'classdef' => nil, 'driver' => nil, 'client_skelton' => nil})

gen.run

