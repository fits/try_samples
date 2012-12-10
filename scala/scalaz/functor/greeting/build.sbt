scalaVersion := "2.10.0-RC3"

scalacOptions += "-Xprint:typer"

libraryDependencies += "org.scalaz" % "scalaz-core" % "7.0.0-M5" cross CrossVersion.full

mainClass in (Compile, run) := Some("fits.sample.Sample")
