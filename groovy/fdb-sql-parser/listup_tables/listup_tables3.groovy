@Grab('com.foundationdb:fdb-sql-parser:1.4.0')
import com.foundationdb.sql.parser.*

def tables = [] as HashSet

// select table name
def tableSelector = [
	skipChildren: { node -> false },
	stopTraversal: { -> false },
	visit: { node -> 
		if (node instanceof FromBaseTable) {
			tables << node.origTableName
		}
		node
	},
	visitChildrenFirst: { node -> false}
] as Visitor

def parser = new SQLParser()

def node = parser.parseStatement(new File(args[0]).text)

node.accept(tableSelector)

tables.each {
	println it
}
