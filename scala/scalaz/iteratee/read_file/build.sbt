scalaVersion := "2.10.1"

scalacOptions += "-Xprint:typer"

libraryDependencies += "org.scalaz" %% "scalaz-iteratee" % "7.0.0"

mainClass in (Compile, run) := Some("fits.sample.ReadLineFileSample3")
