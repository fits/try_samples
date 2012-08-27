scalaVersion := "2.10.0-M7"

scalacOptions += "-Xprint:typer"

resolvers += "Scala Tools" at "https://oss.sonatype.org/content/repositories/releases/"

libraryDependencies += "org.scalaz" % "scalaz-core" % "7.0.0-M3" cross CrossVersion.full

mainClass in (Compile, run) := Some("fits.sample.OrderingSample")
