require 'java'
include_class 'javax.xml.namespace.NamespaceContext'
include_class 'javax.xml.XMLConstants'

#  class NSCT < NamespaceContext
  class NSCT
    include NamespaceContext

    def initialize namespaces
      super()  # Required due to bug JRuby-66
      @namespaces = namespaces
    end

    def getNamespaceURI prefix
      if prefix == 'xml' 
        XMLConstants::XML_NS_URI
      else
        @namespaces[prefix]
      end
    end
  end