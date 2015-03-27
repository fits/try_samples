@GrabConfig(systemClassLoader = true)
@Grab('mysql:mysql-connector-java:5.1.35')
import groovy.sql.Sql

def selectCounter = { dbObj ->
	def res = [:]

	dbObj.eachRow('show global status like ?', ['Com_%']) {
		try {
			def v = it['Value'] as int
			res[it['Variable_name']] = v
		} catch (e) {
		}
	}

	res
}

def db = Sql.newInstance(args[0], 'com.mysql.jdbc.Driver')

def c1 = selectCounter(db)

println 'press key'

System.in.read()

def c2 = selectCounter(db)

db.close()

c2.each { k, v ->
	def diff = v - c1[k]

	if (diff > 0) {
		println "${k}\t${diff}"
	}
}
