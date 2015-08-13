@Grab('org.functionaljava:functionaljava:4.4')
@Grapes([
	@Grab('mysql:mysql-connector-java:5.1.36'),
	@GrabConfig(systemClassLoader = true)
])
import fj.F
import fj.data.Reader

import java.sql.*

def select = { String sql ->
	Reader.unit({ con -> con.prepareStatement(sql) } as F)
}

def runQuery = { proc, PreparedStatement st -> 
	// TODO: nest Reader
	Reader.unit({ con ->  
		def rs = st.executeQuery()

		def result = proc(rs)

		rs.close()
		st.close()

		result
	} as F)
}

def columnToList = { colNum, ResultSet rs -> 
	def res = []

	while(rs.next()) {
		res << rs.getObject(colNum)
	}

	res
}

def close = { res ->
	Reader.unit({ con ->
		con.close()
		res
	} as F )
}

def query = select('select * from product')
	.bind(runQuery.curry(columnToList.curry(2)))
	.bind(close)

println query.f(DriverManager.getConnection(args[0]))
