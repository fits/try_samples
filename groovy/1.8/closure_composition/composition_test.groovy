
def addMsg1 = {s -> s + "-add1"}
def addMsg2 = {s -> s + "-add2"}

def a = addMsg1 >> addMsg2

//ˆÈ‰º‚Í println addMsg2(addMsg1("a")) ‚Æ“¯“™
println a("a")


def n = {it * 3} >> {it + 2}
assert n(3) == 11
println n(3)

def n2 = {it * 3} << {it + 2}
assert n2(3) == 15
println n2(3)

