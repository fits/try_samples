import scala.io.Source

//getLines で取得すると個々の String に改行が含まれるので
//改行を考慮する必要あり
val ErrorLog = """.*\[error\].*\r?\n?""".r

Source.fromFile(args(0), "Shift_JIS").getLines.foreach { l => 

	l match {
		//[error] がある行のみ出力
		case ErrorLog() => print(l)
		case _ =>
	}
}

