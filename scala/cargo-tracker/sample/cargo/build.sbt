ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "3.2.1"

scalacOptions ++= Seq("-Xmax-inlines", "50")

val http4sVer = "0.23.16"

libraryDependencies ++= Seq(
  "org.typelevel" %% "cats-core" % "2.9.0",
  "org.sangria-graphql" %% "sangria" % "3.4.1",
  "org.sangria-graphql" %% "sangria-circe" % "1.3.2",
  "org.http4s" %% "http4s-dsl" % http4sVer,
  "org.http4s" %% "http4s-circe" % http4sVer,
  "org.http4s" %% "http4s-ember-server" % http4sVer,
  "io.circe" %% "circe-generic" % "0.14.3",
  "io.circe" %% "circe-parser" % "0.14.3",
  "org.mongodb.scala" % "mongo-scala-driver_2.13" % "4.8.1",
  "org.scalatest" %% "scalatest" % "3.2.14" % Test
)

