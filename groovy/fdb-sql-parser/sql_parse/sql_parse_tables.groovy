@Grab('com.foundationdb:fdb-sql-parser:1.4.0')
import com.foundationdb.sql.parser.*

def parser = new SQLParser()

def sql = '''
	select
		*
	from
		points p,
		users u
		join address a on
			a.user_id = u.user_id
	where
		u.enable = ?
'''

def node = parser.parseStatement sql

println node.class
println node.resultSetNode.class

node.resultSetNode.fromList.each {
	println it.class
	println it.origTableName
}
