@Grab('org.apache.commons:commons-dbcp2:2.0.1')
@Grab('commons-dbutils:commons-dbutils:1.6')
@Grapes([
	@GrabConfig(systemClassLoader = true),
	@Grab('mysql:mysql-connector-java:5.1.32')
])
import org.apache.commons.dbutils.QueryRunner
import org.apache.commons.dbutils.handlers.MapListHandler
import org.apache.commons.dbcp2.BasicDataSource

def ds = new BasicDataSource(url: 'jdbc:mysql://localhost/information_schema', username: 'root')

def runner = new QueryRunner(ds)

def res = runner.query('select * from engines where support in (?, ?)', new MapListHandler(), 'YES', 'DEFAULT')

println res

println '-----'

res.each {
	println it.ENGINE
}
