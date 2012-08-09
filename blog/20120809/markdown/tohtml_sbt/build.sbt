
scalaVersion := "2.9.2"

libraryDependencies ++= Seq(
	"com.tristanhunt" % "knockoff_2.9.1" % "0.8.0-16",
	"com.github.scala-incubator.io" %% "scala-io-core" % "0.4.0"
)

resolvers += "Typesafe Repo" at "http://repo.typesafe.com/typesafe/releases/"

mainClass in(Compile, run) := Some("fits.sample.ToHtml")
