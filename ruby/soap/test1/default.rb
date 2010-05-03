require 'xsd/qname'

# {http://tempuri.org/}HelloWorld
class HelloWorld
  @@schema_type = "HelloWorld"
  @@schema_ns = "http://tempuri.org/"
  @@schema_qualified = "true"
  @@schema_element = []

  def initialize
  end
end

# {http://tempuri.org/}HelloWorldResponse
class HelloWorldResponse
  @@schema_type = "HelloWorldResponse"
  @@schema_ns = "http://tempuri.org/"
  @@schema_qualified = "true"
  @@schema_element = [["helloWorldResult", ["SOAP::SOAPString", XSD::QName.new("http://tempuri.org/", "HelloWorldResult")]]]

  def HelloWorldResult
    @helloWorldResult
  end

  def HelloWorldResult=(value)
    @helloWorldResult = value
  end

  def initialize(helloWorldResult = nil)
    @helloWorldResult = helloWorldResult
  end
end

# {http://tempuri.org/}CheckCount
class CheckCount
  @@schema_type = "CheckCount"
  @@schema_ns = "http://tempuri.org/"
  @@schema_qualified = "true"
  @@schema_element = [["counter", "SOAP::SOAPInt"], ["msg", "SOAP::SOAPString"]]

  attr_accessor :counter
  attr_accessor :msg

  def initialize(counter = nil, msg = nil)
    @counter = counter
    @msg = msg
  end
end

# {http://tempuri.org/}CheckCountResponse
class CheckCountResponse
  @@schema_type = "CheckCountResponse"
  @@schema_ns = "http://tempuri.org/"
  @@schema_qualified = "true"
  @@schema_element = [["checkCountResult", ["SOAP::SOAPInt", XSD::QName.new("http://tempuri.org/", "CheckCountResult")]]]

  def CheckCountResult
    @checkCountResult
  end

  def CheckCountResult=(value)
    @checkCountResult = value
  end

  def initialize(checkCountResult = nil)
    @checkCountResult = checkCountResult
  end
end

# {http://tempuri.org/}CreateData
class CreateData
  @@schema_type = "CreateData"
  @@schema_ns = "http://tempuri.org/"
  @@schema_qualified = "true"
  @@schema_element = [["name", "SOAP::SOAPString"]]

  attr_accessor :name

  def initialize(name = nil)
    @name = name
  end
end

# {http://tempuri.org/}CreateDataResponse
class CreateDataResponse
  @@schema_type = "CreateDataResponse"
  @@schema_ns = "http://tempuri.org/"
  @@schema_qualified = "true"
  @@schema_element = [["createDataResult", ["Data", XSD::QName.new("http://tempuri.org/", "CreateDataResult")]]]

  def CreateDataResult
    @createDataResult
  end

  def CreateDataResult=(value)
    @createDataResult = value
  end

  def initialize(createDataResult = nil)
    @createDataResult = createDataResult
  end
end

# {http://tempuri.org/}Data
class Data
  @@schema_type = "Data"
  @@schema_ns = "http://tempuri.org/"
  @@schema_element = [["name", ["SOAP::SOAPString", XSD::QName.new("http://tempuri.org/", "Name")]], ["point", ["SOAP::SOAPInt", XSD::QName.new("http://tempuri.org/", "Point")]]]

  def Name
    @name
  end

  def Name=(value)
    @name = value
  end

  def Point
    @point
  end

  def Point=(value)
    @point = value
  end

  def initialize(name = nil, point = nil)
    @name = name
    @point = point
  end
end
