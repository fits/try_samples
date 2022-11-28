ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "3.2.1"

lazy val root = (project in file("."))
  .settings(
    name := "sample",
    libraryDependencies ++= Seq(
      "org.sangria-graphql" %% "sangria" % "3.4.1",
      "org.sangria-graphql" %% "sangria-circe" % "1.3.2"
    )
  )
