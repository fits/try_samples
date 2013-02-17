scalaVersion := "2.10.0"

scalacOptions += "-Xprint:typer"

libraryDependencies += "org.scalaz" %% "scalaz-core" % "7.0.0-M7"

mainClass in (Compile, run) := Some("fits.sample.CounterSample")
