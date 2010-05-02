val config = Map (
	"temp" -> List(("path", "/tmp"), ("encoding", "utf-8")),
	"user" -> List(("path", "/usr"), ("encoding", "shift-jis"))
)

println(config.get("user").flatMap(_.find(x => {
	println(x)
	x._1 == "encoding"
})))


