
def cl1 = {arg1, arg2 ->
	println "owner = $owner"
	println "delegate = $delegate"
	println "arg = $arg1, $arg2"
}

def cl2 = cl1.clone()

cl1(1, 2)

cl2.owner = "test1"
cl2.delegate = "test2"

// owner ‚Í•Ï‚í‚ç‚È‚¢‚ª delegate ‚Íİ’è‚µ‚½’l‚É•Ï‚í‚é
cl2(2, 3)

