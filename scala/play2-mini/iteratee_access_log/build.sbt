scalaVersion := "2.10.0"

//scalacOptions += "-Xprint:typer"

resolvers += "Typesafe Repository" at "http://repo.typesafe.com/typesafe/releases/"

libraryDependencies += "com.typesafe" %% "play-mini" % "2.1-RC2"

mainClass in (Compile, run) := Some("fits.sample.AccessLogParse")
