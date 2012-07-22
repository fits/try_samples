
name := "SampleToHtml"

version := "1.0"

organization := "fits"

scalaVersion := "2.9.2"

libraryDependencies += "com.tristanhunt" % "knockoff_2.9.1" % "0.8.0-16"

mainClass in(Compile, run) := Some("fits.sample.Sample")
