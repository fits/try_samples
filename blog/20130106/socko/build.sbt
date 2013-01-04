scalaVersion := "2.9.2"

resolvers += "Typesafe Repository" at "http://repo.typesafe.com/typesafe/releases/"

libraryDependencies += "org.mashupbots.socko" %% "socko-webserver" % "0.2.3"

mainClass in (Compile, run) := Some("fits.sample.Server")
