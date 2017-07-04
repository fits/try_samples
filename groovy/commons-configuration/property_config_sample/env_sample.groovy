@Grab('commons-configuration:commons-configuration:1.10')
import org.apache.commons.configuration.*

def conf = new PropertiesConfiguration('sample.properties')

println "getProperty=${conf.getProperty('data')}, getString=${conf.getString('data')}"
