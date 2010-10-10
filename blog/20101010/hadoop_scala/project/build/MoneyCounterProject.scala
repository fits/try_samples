import sbt._

class MoneyCounterProject(info: ProjectInfo) extends DefaultProject(info) {

	override def unmanagedClasspath = super.unmanagedClasspath +++ hadoopClasspath +++ hadoopLibClasspath
	override def mainClass = Some("fits.sample.MoneyCounter")

	//環境変数 HADOOP_HOME から Hadoop のホームディレクトリ取得
	lazy val hadoopHomePath = {
		val home = System.getenv("HADOOP_HOME")
		home match {
			case null => error("please set HADOOP_HOME")
			case _ => Path.fromFile(home)
		}
	}

	lazy val hadoopClasspath = hadoopHomePath * "*.jar"
	lazy val hadoopLibClasspath = hadoopHomePath / "lib" * "*.jar"

	lazy val printHadoopSetting = task {
		println("hadoopHomePath: " + hadoopHomePath)
		None
	}
}
