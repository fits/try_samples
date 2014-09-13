@Grab('org.apache.ftpserver:ftpserver-core:1.0.6')
@Grab('org.slf4j:slf4j-api:1.7.7')
@Grab('org.slf4j:slf4j-simple:1.7.7')
import org.apache.ftpserver.FtpServerFactory
import org.apache.ftpserver.usermanager.*

def ftpFactory = new FtpServerFactory()

ftpFactory.userManager = new PropertiesUserManagerFactory(
	file: new File('user.properties'),
	passwordEncryptor: new ClearTextPasswordEncryptor()
).createUserManager()

ftpFactory.createServer().start()
