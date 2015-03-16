@Grab('org.jodd:jodd-lagarto:3.6.5-BETA1')
import jodd.lagarto.dom.LagartoDOMBuilder
import jodd.lagarto.dom.NodeSelector

def doc = new LagartoDOMBuilder().parse(new File(args[0]).text)

def selector = new NodeSelector(doc)

// css selector
selector.select(args[1]).each {
	println it.html
}
