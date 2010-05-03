require 'default.rb'

require 'soap/rpc/driver'

class ServiceSoap < ::SOAP::RPC::Driver
  DefaultEndpointUrl = "http://localhost:1099/SimpleTest/Service.asmx"
  MappingRegistry = ::SOAP::Mapping::Registry.new

  Methods = [
    [ "http://tempuri.org/HelloWorld",
      "helloWorld",
      [ ["in", "parameters", ["::SOAP::SOAPElement", "http://tempuri.org/", "HelloWorld"], true],
        ["out", "parameters", ["::SOAP::SOAPElement", "http://tempuri.org/", "HelloWorldResponse"], true] ],
      { :request_style =>  :document, :request_use =>  :literal,
        :response_style => :document, :response_use => :literal }
    ],
    [ "http://tempuri.org/CheckCount",
      "checkCount",
      [ ["in", "parameters", ["::SOAP::SOAPElement", "http://tempuri.org/", "CheckCount"], true],
        ["out", "parameters", ["::SOAP::SOAPElement", "http://tempuri.org/", "CheckCountResponse"], true] ],
      { :request_style =>  :document, :request_use =>  :literal,
        :response_style => :document, :response_use => :literal }
    ],
    [ "http://tempuri.org/CreateData",
      "createData",
      [ ["in", "parameters", ["::SOAP::SOAPElement", "http://tempuri.org/", "CreateData"], true],
        ["out", "parameters", ["::SOAP::SOAPElement", "http://tempuri.org/", "CreateDataResponse"], true] ],
      { :request_style =>  :document, :request_use =>  :literal,
        :response_style => :document, :response_use => :literal }
    ]
  ]

  def initialize(endpoint_url = nil)
    endpoint_url ||= DefaultEndpointUrl
    super(endpoint_url, nil)
    self.mapping_registry = MappingRegistry
    init_methods
  end

private

  def init_methods
    Methods.each do |definitions|
      opt = definitions.last
      if opt[:request_style] == :document
        add_document_operation(*definitions)
      else
        add_rpc_operation(*definitions)
        qname = definitions[0]
        name = definitions[2]
        if qname.name != name and qname.name.capitalize == name.capitalize
          ::SOAP::Mapping.define_singleton_method(self, qname.name) do |*arg|
            __send__(name, *arg)
          end
        end
      end
    end
  end
end

