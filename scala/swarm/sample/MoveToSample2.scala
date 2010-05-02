import swarm._
import java.net.InetAddress

//リモートプロセスで継続処理を実行した後、
//ローカルプロセス側に残りの継続処理が返ってくるサンプル
object MoveToSample2 {

	def main(args: Array[String]) = {
		Swarm.listen(9997)
		Swarm.spawn(proc)
	}

	def proc(u: Unit) = {
		var i = 0
		println("start proc : " + i)

		Swarm.moveTo(new Location(InetAddress.getLocalHost(), 9998))

		//リモートプロセスで実行
		i = i + 5
		println("remote proc : " + i)

		Swarm.moveTo(new Location(InetAddress.getLocalHost(), 9997))

		//ローカルプロセスで実行
		i = i + 10
		println("end proc : " + i)

		NoBee()
	}
}
