@Grapes([
    @Grab("com.h2database:h2:1.3.163"),
    @GrabConfig(systemClassLoader = true)
])
import groovy.sql.Sql

class Func {
	static String toChar(int src) {
		println "toChar : int ${src}"
		src.toString()
	}

	static String toChar(String dateString, String dateFormat) {
		if (dateString == null) {
			return dateString
		}

		dateFormat = dateFormat.toLowerCase()
							.replaceAll("mm", "MM")
							.replaceAll("hh24", "HH")
							.replaceAll("mi", "mm")

		println "toChar : ${dateString}, ${dateFormat}"

		def dateCls = (dateString.length() > 10)? java.sql.Timestamp: java.sql.Date
		dateCls.valueOf(dateString).format(dateFormat)
	}
}

//インメモリDBとして H2 を使用
def db = Sql.newInstance("jdbc:h2:mem:", "org.h2.Driver")

db.execute("create table TDATA as select * from CSVREAD('testdata.csv')")
//以下でも可
//db.execute("create table TDATA as select * from CSVREAD('classpath:/testdata.csv')")


//TO_CHAR をユーザー定義関数として定義
db.execute('CREATE ALIAS TO_CHAR FOR "Func.toChar"')

def sql = '''
	select
		TO_CHAR(no) as no, 
		title,
		TO_CHAR(create_datetime, 'yyyy-mm-dd hh24:mi:ss') as cdatetime,
		TO_CHAR(create_date, 'yyyy-mm-dd hh24:mi:ss') as cdate
	from TDATA
'''

db.eachRow(sql) {r ->
	println "検索結果 : ${r.no}, ${r.title}, ${r.cdatetime}, ${r.cdate}"
}
