scalaVersion := "2.10.1"

scalacOptions += "-Xprint:typer"

libraryDependencies ++= Seq(
	"org.mybatis.scala" %% "mybatis-scala-core" % "1.0.1", 
	"mysql" % "mysql-connector-java" % "5.1.25"
)

mainClass in (Compile, run) := Some("fits.sample.MyBatisSample")
