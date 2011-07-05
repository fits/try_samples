name := "ScalaQuery test project"

version := "1.0"

organization := "fits"

scalaVersion := "2.9.0-1"

libraryDependencies ++= Seq(
//	"org.scalaquery" %% "scalaquery" % "0.9.4",
	"org.scalaquery" % "scalaquery_2.9.0" % "0.9.4",
	"com.h2database" % "h2" % "1.3.157"
)

mainClass in(Compile, run) := Some("fits.sample.Sample")
