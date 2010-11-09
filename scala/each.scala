
(1 to 10).foreach {
    case 1 => println("one")
    case x: int if x % 2 == 0 => println("偶数 " + x)
    case _ => println("other")
}
