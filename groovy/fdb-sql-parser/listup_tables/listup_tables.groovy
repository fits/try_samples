@Grab('com.foundationdb:fdb-sql-parser:1.4.0')
import com.foundationdb.sql.parser.*

// print table name
def tablePrinter = [
	skipChildren: { node -> false },
	stopTraversal: { -> false },
	visit: { node -> 
		if (node instanceof FromBaseTable) {
			println "table: ${node.tableName}, ${node.origTableName}"
		}

		node
	},
	visitChildrenFirst: { node -> false}
] as Visitor

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

node.accept(tablePrinter)
