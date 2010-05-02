val ValidLog = """^(\w+)_(\d+)\.log$""".r

def checkLog(fileName: String) = {
	fileName match {
		case ValidLog(host, date) => println("valid: " + host + ", " + date)
		case _ => println("invalid: " + fileName)
	}
}

checkLog("test.log")
checkLog("node1_20090130.log")
checkLog("node1_20090130-aa.log")

