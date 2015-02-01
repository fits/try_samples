@Grapes([
	@Grab('mysql:mysql-connector-java:5.1.34'),
	@GrabConfig(systemClassLoader = true)
])
import groovy.sql.Sql

if (args.length < 1) {
	println '<db url>'
	return
}

def db = Sql.newInstance(args[0])

db.eachRow('show tables') {
	def table = it[0]

	db.eachRow("show create table ${table}" as String) { d ->
		println "${d[1]};"
		println ''
	}
}
