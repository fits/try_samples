scalaVersion := "2.10.0"

scalacOptions += "-Xprint:typer"

mainClass in (Compile, run) := Some("fits.sample.Sample")
