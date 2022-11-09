ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "3.2.1"

lazy val root = (project in file("."))
  .settings(
    name := "sample_scala3",
    libraryDependencies += "org.typelevel" %% "cats-core" % "2.8.0"
  )
