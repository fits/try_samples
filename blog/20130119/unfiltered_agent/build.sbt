scalaVersion := "2.10.0"

//scalacOptions += "-Xprint:typer"

resolvers += "Typesafe Repository" at "http://repo.typesafe.com/typesafe/releases/"

libraryDependencies ++= Seq(
	"net.databinder" %% "unfiltered-filter" % "0.6.5",
	"net.databinder" %% "unfiltered-netty-websockets" % "0.6.5",
	"com.typesafe.akka" %% "akka-agent" % "2.1.0"
)

mainClass in (Compile, run) := Some("fits.sample.SampleApp2")
