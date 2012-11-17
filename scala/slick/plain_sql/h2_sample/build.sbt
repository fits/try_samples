scalaVersion := "2.10.0-RC2"

scalacOptions += "-Xprint:typer"

libraryDependencies ++= Seq(
	"com.typesafe" % "slick" % "0.11.2" cross CrossVersion.full,
	"com.h2database" % "h2" % "1.3.168"
)

mainClass in (Compile, run) := Some("fits.sample.Sample")
