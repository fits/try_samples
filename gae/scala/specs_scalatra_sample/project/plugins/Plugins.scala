import sbt._

class Plugins(info: ProjectInfo) extends PluginDefinition(info) {
	val appenginePlugin = "net.stbbs.yasushi" % "sbt-appengine-plugin" % "2.1"
}
