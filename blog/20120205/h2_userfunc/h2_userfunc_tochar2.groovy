@Grapes([
    @Grab("com.h2database:h2:1.3.164"),
    @GrabConfig(systemClassLoader = true)
])
import groovy.sql.Sql

//インメモリDBとして H2 を使用
def db = Sql.newInstance("jdbc:h2:mem:", "org.h2.Driver")

db.execute("create table TDATA as select * from CSVREAD('testdata.csv')")

/*
 TO_CHAR をユーザー定義関数として定義
 オーバーロードはサポートしていないので可変長パラメータを使う
*/
db.execute('''CREATE ALIAS TO_CHAR AS $$
import java.text.SimpleDateFormat;
import java.util.Date;
@CODE
String toChar(String... param) {
	String value = param[0];
	String dateFormat = (param.length > 1)? param[1]: null;

	if (value == null || dateFormat == null) {
		System.out.println("toChar : " + value);
		return value;
	}

	dateFormat = dateFormat.toLowerCase()
					.replaceAll("mm", "MM")
					.replaceAll("hh24", "HH")
					.replaceAll("mi", "mm");

	System.out.println("toChar : " + value + ", " + dateFormat);

	Date date = (value.length() > 10)? java.sql.Timestamp.valueOf(value): java.sql.Date.valueOf(value);

	return new SimpleDateFormat(dateFormat).format(date);
}
$$;
''')


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
