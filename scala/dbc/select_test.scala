import scala.dbc._
import scala.dbc.syntax.DataTypeUtil._
import scala.dbc.syntax.Statement._
import java.net.URI

//MySQL用の Vender クラス作成
case class MySQL(val uri: java.net.URI, user: String, pass: String) extends Vendor {
	val urlProtocolString = "jdbc:mysql:"
	val nativeDriverClass = Class.forName("com.mysql.jdbc.Driver")
	val retainedConnections = 5
}

val db = new Database(MySQL(new URI("jdbc:mysql://localhost/test"), "root", ""))
try {
	//select で SelectZygote
	//fields で SelectOf、
	//from で SelectBeyond 生成
	//fields の中では 文字列から SelectDerivedField を経て、
	//SelectDerivedColumns への変換が行われる
	val st = select fields {
				("name" of characterVarying(40)) and ("point" of integer)
			} from "test1"

	//statement.Select に変換されて実行
	val rows = db.executeStatement(st)

	rows foreach {r =>
		r.fields foreach {f =>
			print(f.content.sqlString + ", ")
		}
		println()
	}
}
catch {
	case e: Exception => e.printStackTrace
}

