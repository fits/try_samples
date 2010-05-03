require 'java'
require 'XPath'
require 'ESB'
include_class "javax.xml.namespace.QName"

@@service = QName.new "http://fuji.dev.java.net/application/[app]", "[service]"

def process(inMsg)
  doc = inMsg.getPayload
  # your code goes here

  node = XPath.findNode(doc, "//data[@id=1]")

  if node
    node.getParentNode().removeChild(node)
  end

  # return message  
  inMsg
end