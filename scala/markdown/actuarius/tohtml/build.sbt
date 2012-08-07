
scalaVersion := "2.9.2"

resolvers += "Christophs Maven Repo" at "http://maven.henkelmann.eu/"

libraryDependencies += "eu.henkelmann" % "actuarius_2.9.1" % "0.2.3"

mainClass in(Compile, run) := Some("fits.sample.Sample")
