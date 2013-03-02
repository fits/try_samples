@Grapes([
	@Grab('org.xerial:sqlite-jdbc:3.7.2'),
	@GrabConfig(systemClassLoader = true)
])
import groovy.sql.Sql

def sql = Sql.newInstance('jdbc:sqlite:sample.db', null, null, 'org.sqlite.JDBC')

sql.execute 'drop table CUSTOMER'

sql.execute '''
	create table CUSTOMER (
		id string,
		name string
	)
'''

(1..10).each {
	sql.execute('insert into CUSTOMER values (?, ?)', [it, "test-$it"])
}

sql.eachRow('select * from CUSTOMER') {
	println it
}

sql.close()

