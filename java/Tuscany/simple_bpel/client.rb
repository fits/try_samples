include Java

import "org.apache.tuscany.sca.host.embedded.SCADomain"
import "fits.sample.SampleTest"

domain = SCADomain.newInstance("SampleTest.composite")

service = domain.getService(SampleTest.java_class, "SampleTestComponent")

puts service.hello("abc")

domain.close
