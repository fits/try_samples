include Java

import org.apache.tuscany.sca.host.embedded.SCADomain;

scaDomain = SCADomain.newInstance("SampleTest.composite")

puts "start service"

gets

scaDomain.close

puts "stop"
