@Grab('org.apache.ftpserver:ftpserver-core:1.0.6')
@Grab('org.slf4j:slf4j-api:1.7.7')
@Grab('org.slf4j:slf4j-simple:1.7.7')
import org.apache.ftpserver.FtpServerFactory
import org.apache.ftpserver.listener.ListenerFactory
import org.apache.ftpserver.ssl.SslConfigurationFactory
import org.apache.ftpserver.usermanager.*

def ftpFactory = new FtpServerFactory()

def listenerFactory = new ListenerFactory(port: 2221)

def ssl = new SslConfigurationFactory(
	keystoreFile: new File('sample.jks'),
	keystorePassword: 'sample'
)

listenerFactory.sslConfiguration = ssl.createSslConfiguration()

ftpFactory.addListener('default', listenerFactory.createListener())


ftpFactory.userManager = new PropertiesUserManagerFactory(
	file: new File('user.properties'),
	passwordEncryptor: new ClearTextPasswordEncryptor()
).createUserManager()

ftpFactory.createServer().start()

