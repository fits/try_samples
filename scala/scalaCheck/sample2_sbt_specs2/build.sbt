name := "scalaCheck sample2"

organization := "fits"

version := "1.0-SNAPSHOT"

scalaVersion := "2.9.1"

libraryDependencies ++= Seq(
	"org.specs2" %% "specs2" % "1.8.2" % "test",
	"org.scala-tools.testing" %% "scalacheck" % "1.9"
)

testFrameworks += TestFrameworks.Specs2
