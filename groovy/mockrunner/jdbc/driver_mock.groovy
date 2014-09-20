@Grapes([
	@Grab('com.mockrunner:mockrunner-jdbc:1.0.3'),
	@GrabExclude('xml-apis#xml-apis;1.3.04'),
	@GrabConfig(systemClassLoader = true)
])
import com.mockrunner.mock.jdbc.JDBCMockObjectFactory

import java.sql.DriverManager

def printDrivers = {
	DriverManager.drivers.each {
		println it
	}
}

printDrivers()

def factory = new JDBCMockObjectFactory()

println '-----'

printDrivers()

println DriverManager.getConnection('jdbc:mysql://localhost/sample?characterEncoding=utf8')
