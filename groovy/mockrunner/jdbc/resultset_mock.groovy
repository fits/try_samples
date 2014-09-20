@Grapes([
	@Grab('com.mockrunner:mockrunner-jdbc:1.0.3'),
	@GrabExclude('xml-apis#xml-apis;1.3.04'),
	@GrabConfig(systemClassLoader = true)
])
import com.mockrunner.mock.jdbc.JDBCMockObjectFactory
import java.sql.DriverManager

def factory = new JDBCMockObjectFactory()

def handler = factory.mockConnection.statementResultSetHandler

def mockRs = handler.createResultSet()
mockRs.addColumn('name', ['a', 'b'])
mockRs.addColumn('value', [1, 2])

handler.prepareGlobalResultSet(mockRs)

def con = DriverManager.getConnection('jdbc:mysql://localhost/sample?characterEncoding=utf8')

def st = con.createStatement()
def rs = st.executeQuery('select * from samples')

println rs

println '-----'

while (rs.next()) {
	println rs.toRowResult()
}

println '-----'

def mrs = rs.metaData

(0..mrs.columnCount).each {
	println "${mrs.getColumnName(it)}, ${mrs.getColumnTypeName(it)}, ${mrs.getPrecision(it)}, ${mrs.getTableName(it)}"
}

