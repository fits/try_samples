
object book {

  import scala.xml.Node

  def add( p: Node, newEntry: Node ): Node = p match {

      case <book>{ ch }</book> => 
        <book>{ ch }{ newEntry }</book>
      case _ => <empty />
  }

  val pb2 = 
    add(
        <book>ab</book>, 
         <entry>
           <name>test</name> 
         </entry>
    )

  def main( args: Array[String] ) = 
    Console.println( pb2 )
}

book.main(args)
