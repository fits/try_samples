
interface Tester {
	void check(String name)
	int checkPoint(String a, int b)
}

def tester = [check: {a -> println a}, checkPoint: {a, b -> println "${a}, ${b}"; 10 + b}] as Tester

tester.check("abc")
def res = tester.checkPoint("チェック", 6)

println res

