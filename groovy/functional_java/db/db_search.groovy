@Grab('org.functionaljava:functionaljava:4.2-beta-1')
@Grab('mysql:mysql-connector-java:5.1.32')
import fj.F
import fj.control.db.DB
import fj.control.db.DbState
import fj.data.Option

import java.sql.Connection
import java.sql.ResultSet

def dbs = DbState.reader args[0]

def select = { String sql, Connection con -> 
	con.prepareStatement(sql).executeQuery()
}

def firstResult = { ResultSet rs -> 
	def result = rs.next()? Option.some(rs.getObject(1)): Option.none()
	rs.close()
	result
}

println dbs

def q = DB.db( select.curry('select count(*) from customers') ).map(firstResult)

// DbState.run() の中でコネクションの取得・実行・コミット（ロールバック）・クローズを実施
println dbs.run(q)

