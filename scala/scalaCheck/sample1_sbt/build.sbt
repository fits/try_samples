name := "scalaCheck sample1"

organization := "fits"

version := "1.0-SNAPSHOT"

scalaVersion := "2.9.1"

libraryDependencies += "org.scala-tools.testing" %% "scalacheck" % "1.9"

mainClass in (Compile, run) := Some("fits.sample.SampleSpecification")
