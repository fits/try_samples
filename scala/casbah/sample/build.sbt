scalaVersion := "2.10.0"

scalacOptions += "-Xprint:typer"

libraryDependencies += "org.mongodb" %% "casbah" % "2.5.0"

mainClass in (Compile, run) := Some("fits.sample.Sample")
