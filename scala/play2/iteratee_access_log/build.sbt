scalaVersion := "2.10.0"

scalacOptions += "-Xprint:typer"

resolvers += "Typesafe Repository" at "http://repo.typesafe.com/typesafe/releases/"

libraryDependencies += "play" %% "play-iteratees" % "2.1.0"

mainClass in (Compile, run) := Some("fits.sample.AccessLogParse")
