import swarm._
import java.net.InetAddress

object MoveToSample {

	def main(args: Array[String]) = {
	//	Swarm.listen(9997)
		//listen の使用避けるには myLocation を設定しておく
		Swarm.myLocation = new Location(InetAddress.getLocalHost(), 0)
		Swarm.spawn(proc)
	}

	def proc(u: Unit) = {
		println("start proc")

		Swarm.moveTo(new Location(InetAddress.getLocalHost(), 9998))

		println("end proc")
		NoBee()
	}
}
