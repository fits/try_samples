enablePlugins(ScalaNativePlugin)

ThisBuild / scalaVersion := "3.2.1"

libraryDependencies ++= Seq(
  "io.github.cquiroz" %%% "scala-java-time" % "2.5.0",
  "org.typelevel" %%% "cats-core" % "2.8.0",
  "org.typelevel" %%% "cats-free" % "2.8.0"
)
