@Grab('org.functionaljava:functionaljava:4.4')
@Grab('mysql:mysql-connector-java:5.1.36')
import fj.F
import fj.control.db.DB
import fj.control.db.DbState
import fj.data.Option

import java.sql.Connection
import java.sql.ResultSet

def dbs = DbState.reader args[0]

def select = { String sql, Connection con -> 
	con.prepareStatement(sql)
}

def headFirstColumn = { ResultSet rs ->
	rs.next() ? Option.some(rs.getObject(1)) : Option.none()
}

println dbs

def q = DB.db( select.curry('select count(*) from product') ).bind { ps -> 
	DB.unit( ps.executeQuery() ).bind { rs ->
		DB.unit( headFirstColumn(rs) ).map { res ->
			rs.close()
			ps.close()

			res
		}
	}
}

println dbs.run(q)
