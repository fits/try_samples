@Grab('com.foundationdb:fdb-sql-parser:1.4.0')
import com.foundationdb.sql.parser.SQLParser

def parser = new SQLParser()

def sql = '''
	select
		*
	from
		users u
		join address a on
			a.user_id = u.user_id
	where
		u.enable = ?
'''

def node = parser.parseStatement sql

println node

println '---'

node.treePrint()
