@Grapes([
    @Grab("com.h2database:h2:1.3.164"),
    @GrabConfig(systemClassLoader = true)
])
import groovy.sql.Sql

//インメモリDBとして H2 を使用
def db = Sql.newInstance("jdbc:h2:mem:", "org.h2.Driver")

db.execute("create table TDATA as select * from CSVREAD('testdata.csv')")
//以下でも可
//db.execute("create table TDATA as select * from CSVREAD('classpath:/testdata.csv')")

class Func {
	//数値の文字列化
	static String toChar(int value) {
		println "toChar : int ${value}"
		value.toString()
	}

	/* 上記は以下でも可
	static String toChar(String value) {
		println "toChar : ${value}"
		value
	}
	*/

	//日付の文字列化
	static String toChar(Date value, String dateFormat) {
		if (value == null) {
			return value
		}

		//Oracle の日付フォーマットを Java 用に変換（一部のみ対応）
		dateFormat = dateFormat.toLowerCase()
								.replaceAll("mm", "MM")
								.replaceAll("hh24", "HH")
								.replaceAll("mi", "mm")

		println "toChar : ${value}, ${dateFormat}"

		value.format(dateFormat)
	}
}

// Func.toChar を TO_CHAR ユーザー定義関数として登録
db.execute('CREATE ALIAS TO_CHAR FOR "Func.toChar"')

def sql = '''
	select
		TO_CHAR(no) as no, 
		title,
		TO_CHAR(create_datetime, 'yyyy/mm/dd hh24:mi:ss') as cdatetime,
		TO_CHAR(create_date, 'yyyy/mm/dd') as cdate
	from TDATA
'''

db.eachRow(sql) {r ->
	println "検索結果 : ${r.no}, ${r.title}, ${r.cdatetime}, ${r.cdate}"
}
