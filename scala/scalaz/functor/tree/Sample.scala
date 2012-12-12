package fits.sample

import scalaz._
import Scalaz._

object Sample extends App {

	sealed trait Tree[A]
	case class Node[A](left: Tree[A], right: Tree[A]) extends Tree[A]
	case class Leaf[A](a: A) extends Tree[A]

	trait TreeInstances {
		implicit val treeInstance = new Functor[Tree] {
			def map[A, B](fa: Tree[A])(f: A => B): Tree[B] = {
				fa match {
					case Node(left, right) => Node(map(left)(f), map(right)(f))
					case Leaf(a) => Leaf(f(a))
				}
			}
		}
	}

	case object Tree extends TreeInstances

	val n = Node(Node(Leaf("a"), Node(Leaf("bb"), Leaf("ccc"))), Leaf("dddd"))

	n |> println

	Functor[Tree].map(n) {(a) => s"${a} !!!"} |> println

	Functor[Tree].map(n) {(a) => a.length} |> println
}
