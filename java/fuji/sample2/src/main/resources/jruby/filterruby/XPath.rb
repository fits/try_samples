require 'java'
require 'NSCT'
include_class "javax.xml.xpath.XPathFactory"
include_class "javax.xml.xpath.XPathConstants"
#include_class "javax.xml.xpath.XPath"


  class XPath
    @@xpf = nil

    #def XPath.match node, path, namespaces={}
    #  node = fixNode node
    #  xp = XPath.newXP namespaces
    #  collect xp.evaluate(path, node, XPathConstants::NODESET)
    #end

    #def XPath.each node, path, namespaces = {}
    #  #node = fixNode node
    #  xp = XPath.newXP namespaces
    #  list = xp.evaluate(path, node, XPathConstants::NODESET)
    #  collect(list).each { |node| yield node }
    #end
    
    
    def XPath.findNode node, path, namespaces = {}
      xp = XPath.newXP namespaces
      xp.evaluate(path, node, XPathConstants::NODE)
    end

    def XPath.findString node, path, namespaces = {}
      xp = XPath.newXP namespaces
      xp.evaluate(path, node, XPathConstants::STRING)
    end
    
    def XPath.newXP namespaces
      raise "The namespaces argument, if supplied, must be a hash object." unless namespaces.kind_of? Hash
      @@xpf ||= XPathFactory.newInstance
      #xpf = XPathFactory.newInstance
      xp = @@xpf.newXPath

      xp.namespaceContext= NSCT.new namespaces
      return xp
    end

  end