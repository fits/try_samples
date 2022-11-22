ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "3.2.1"

lazy val root = (project in file("."))
  .settings(
    name := "sample",
    libraryDependencies += "org.sangria-graphql" %% "sangria" % "3.4.1"
  )
