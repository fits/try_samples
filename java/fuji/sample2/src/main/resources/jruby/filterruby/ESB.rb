require 'java'
require 'XPath'
include_class "com.sun.jbi.fuji.components.jruby.ServiceConsumerFactory"

  class ESB    
    def ESB.invoke service, operation, payload
      consumer = ServiceConsumerFactory.newServiceConsumer(service)
      if consumer
        msg = consumer.getServiceMessageFactory.createMessage
        msg.setPayload(payload)
        consumer.call(operation, msg)
      else
        puts "Unable to locate service: " + service.toString
        nil
      end
    end
  end