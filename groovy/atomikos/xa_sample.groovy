@GrabConfig(systemClassLoader = true)
@Grab('com.atomikos:transactions-jdbc:4.0.0M5')
@Grab('mysql:mysql-connector-java:5.1.38')
@Grab('javax:javaee-api:7.0')
import com.atomikos.icatch.jta.UserTransactionManager
import com.atomikos.jdbc.AtomikosDataSourceBean

def createDataSource = { id, user, url ->
	def p = new Properties()
	p.user = user
	p.url = url

	def ds = new AtomikosDataSourceBean()
	ds.uniqueResourceName = id
	ds.xaDataSourceClassName = 'com.mysql.jdbc.jdbc2.optional.MysqlXADataSource'
	ds.xaProperties = p
	ds.poolSize = 1

	ds
}

def insertData = { con, r ->
	def st = con.prepareStatement('insert into sample (v) values (?)')

	st.setInt(1, r)

	def res = st.executeUpdate()

	println "result: ${res}"

	st.close()
}

def tm = new UserTransactionManager()
tm.init()

def ds1 = createDataSource('mysql1', 'root', 'jdbc:mysql://localhost/d1?characterEncoding=utf8')
ds1.init()

def ds2 = createDataSource('mysql2', 'root', 'jdbc:mysql://localhost/d2?characterEncoding=utf8')
ds2.init()

tm.begin()

try {
	def c1 = ds1.getConnection()
	def c2 = ds2.getConnection()

	insertData(c1, 1)
	insertData(c2, 2)

	println '-----------------'

	//sleep(10000)

	c2.close()
	c1.close()

	tm.commit()

} catch (ex) {
	println ex

	tm.rollback()
}

ds1.close()
ds2.close()

tm.close()
