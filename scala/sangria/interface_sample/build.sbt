ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "3.2.1"

libraryDependencies ++= Seq(
  "org.sangria-graphql" %% "sangria" % "3.4.1",
  "org.sangria-graphql" %% "sangria-circe" % "1.3.2",
  "org.typelevel" %% "cats-core" % "2.9.0"
)
