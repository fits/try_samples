
scalaVersion := "2.9.2"

scalacOptions += "-Xprint:typer"

resolvers += "Scala Tools Snapshots" at "http://scala-tools.org/repo-snapshots/"

libraryDependencies += "org.scalaz" %% "scalaz-core" % "6.0.4"

mainClass in (Compile, run) := Some("fits.sample.Sample")
