
val scala3Version = "3.1.0"

lazy val root = project
  .in(file("."))
  .settings(
    name := "sample",
    version := "0.1.0",

    scalaVersion := scala3Version,
  )
