
scalaVersion := "2.9.2"

libraryDependencies ++= Seq(
	"net.databinder" %% "unfiltered-filter" % "0.6.2",
	"net.databinder" %% "unfiltered-jetty" % "0.6.2"
)

mainClass in (Compile, run) := Some("fits.sample.SampleServer")
