import scala.concurrent.ops

println("1")

//非同期実行
ops.spawn {
	println("3: test")
}

println("2")
