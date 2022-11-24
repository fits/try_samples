ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "3.2.1"

val http4sVer = "0.23.16"

lazy val root = (project in file("."))
  .settings(
    name := "sample",
    libraryDependencies ++= Seq(
      "org.http4s" %% "http4s-dsl" % http4sVer,
      "org.http4s" %% "http4s-ember-server" % http4sVer
    )
  )
