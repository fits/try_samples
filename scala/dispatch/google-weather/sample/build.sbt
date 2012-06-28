
scalaVersion := "2.9.2"

scalacOptions += "-Xprint:typer"

libraryDependencies ++= Seq(
	"net.databinder" %% "dispatch-http" % "0.8.8"
)

mainClass in (Compile, run) := Some("fits.sample.Sample")
