scalaVersion := "2.10.0"

scalacOptions += "-Xprint:typer"

libraryDependencies += "org.reactivemongo" %% "reactivemongo" % "0.8"

mainClass in (Compile, run) := Some("fits.sample.Sample")
