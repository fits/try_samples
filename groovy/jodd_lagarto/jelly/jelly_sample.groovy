@Grab('org.jodd:jodd-lagarto:3.6.5-BETA1')
import jodd.jerry.Jerry
import jodd.jerry.JerryFunction

def doc = Jerry.jerry(new File(args[0]).text)

doc.$(args[1]).each({ trg, index ->
	println '-----'
	println trg.text()
	println index

	true
} as JerryFunction)

