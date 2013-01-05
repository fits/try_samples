scalaVersion := "2.9.2"

resolvers += "Typesafe Repository" at "http://repo.typesafe.com/typesafe/releases/"

libraryDependencies += "com.typesafe" %% "play-mini" % "2.0.3"

mainClass in (Compile, run) := Some("play.core.server.NettyServer")
