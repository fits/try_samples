import scala.concurrent.ops

println("1")

//非同期実行
val f = ops.future {
	println("3: test")
	1
}

//Thread.sleep(1000)

println("2")

printf("num = %d", f())
