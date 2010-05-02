require 'soap/wsdlDriver'

wsdl = 'http://localhost:8080/ode/processes/travelReservation?wsdl'

service = SOAP::WSDLDriverFactory.new(wsdl).create_rpc_driver
service.generate_explicit_type = true

req = {
	"customerName" => "test1", 
	"dateFrom" => "2008-08-20", 
	"dateTo" => "2008-08-25",
	"doCarRental" => 1
}

res = service.reserve(req)

puts "hotel reservation = #{res.hotelReservation}, car reservation = #{res.rentalCarReservation}"
