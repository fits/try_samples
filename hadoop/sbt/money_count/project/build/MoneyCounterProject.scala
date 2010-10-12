import sbt._

class MoneyCounterProject(info: ProjectInfo) extends DefaultProject(info) {

	override def unmanagedClasspath = super.unmanagedClasspath +++ hadoopClasspath +++ hadoopLibClasspath
	override def mainClass = Some("fits.sample.MoneyCounter")

	lazy val hadoopHomePath = {
		val home = System.getenv("HADOOP_HOME")
		home match {
			case null => error("please set HADOOP_HOME")
			case _ => Path.fromFile(home)
		}
	}

	lazy val hadoopClasspath = hadoopHomePath * "*.jar"
	lazy val hadoopLibClasspath = hadoopHomePath / "lib" * "*.jar"

	lazy val printSetting = task {
		println("hadoopHomePath: " + hadoopHomePath)
		println("jarPath: " + jarPath)
		println("runClasspath: " + runClasspath)
		println("unmanagedClasspath: " + unmanagedClasspath)
		println("scala lib jar: " + FileUtilities.scalaLibraryJar)

		None
	}

}
