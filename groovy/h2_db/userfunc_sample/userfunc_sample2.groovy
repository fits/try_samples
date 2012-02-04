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


/*
 TO_CHAR をユーザー定義関数として定義
 オーバーロードはサポートしていないので単一のメソッドを登録する
*/
db.execute('''CREATE ALIAS TO_CHAR AS $$
import java.text.SimpleDateFormat;
import java.util.Date;
@CODE
String toChar(String dateString, String dateFormat) {
	if (dateString == null) {
		return dateString;
	}

	dateFormat = dateFormat.toLowerCase()
					.replaceAll("mm", "MM")
					.replaceAll("hh24", "HH")
					.replaceAll("mi", "mm");

	System.out.println("toChar : " + dateString + ", " + dateFormat);

	Date date = (dateString.length() > 10)? java.sql.Timestamp.valueOf(dateString): java.sql.Date.valueOf(dateString);

	return new SimpleDateFormat(dateFormat).format(date);
}
$$;
''')


def sql = '''
	select
		no, 
		title,
		TO_CHAR(create_datetime, 'yyyy/mm/dd hh24:mi:ss') as cdatetime,
		TO_CHAR(create_date, 'yyyy/mm/dd') as cdate
	from TDATA
'''

db.eachRow(sql) {r ->
	println "検索結果 : ${r.no}, ${r.title}, ${r.cdatetime}, ${r.cdate}"
}
