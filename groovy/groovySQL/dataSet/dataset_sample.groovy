@Grapes([
	@Grab('org.postgresql:postgresql:9.3-1101-jdbc41'),
	@GrabConfig(systemClassLoader = true)
])
import groovy.sql.*

def db = Sql.newInstance('jdbc:postgresql://localhost/sampledb', 'user1', '', 'org.postgresql.Driver')

def customer = new DataSet(db, 'customer')

customer.findAll { it.point > 5 }.each {
	println it
}

db.close()