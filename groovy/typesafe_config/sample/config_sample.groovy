@Grab('com.typesafe:config:1.3.1')
import com.typesafe.config.ConfigFactory

def conf = ConfigFactory.load()

println conf.getString('sample.name')

def sample = conf.getConfig('sample')

println sample.getString('name')

println sample.getString('value')
