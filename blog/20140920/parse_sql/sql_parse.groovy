@Grab('com.foundationdb:fdb-sql-parser:1.4.0')
import com.foundationdb.sql.parser.SQLParser

def parser = new SQLParser()

def node = parser.parseStatement(new File(args[0]).text)

node.treePrint()
