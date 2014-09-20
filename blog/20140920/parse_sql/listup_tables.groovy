@Grab('com.foundationdb:fdb-sql-parser:1.4.0')
import com.foundationdb.sql.parser.FromBaseTable
import com.foundationdb.sql.parser.SQLParser
import com.foundationdb.sql.parser.Visitor

def tables = [] as TreeSet

// select table name
def tableSelector = [
	visit: { node -> 
		if (node instanceof FromBaseTable) {
			// get original table name
			tables << node.origTableName.fullTableName
		}
		node
	},
	skipChildren: { node -> false },
	stopTraversal: { -> false },
	visitChildrenFirst: { node -> false}
] as Visitor

def parser = new SQLParser()

def node = parser.parseStatement(new File(args[0]).text)

node.accept(tableSelector)

tables.each {
	println it
}
