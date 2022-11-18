ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "3.2.1"

lazy val root = (project in file("."))
  .settings(
    name := "model1",
    libraryDependencies ++= Seq(
      "org.typelevel" %% "cats-core" % "2.8.0",
      "org.typelevel" %% "cats-free" % "2.8.0",
      "org.scalatest" %% "scalatest" % "3.2.14" % Test
    )
  )
