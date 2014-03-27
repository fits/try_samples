
def test1 = {}

test2 = {}

func = test1 >> test2

def sample1() {}
void sample2() {}
static void sample3() {}

name1 = 'abc'
def name2 = 'aaa'
String name3 = 'aaa'

// args, test2, func, name1
this.binding.variables.each {k, v ->
	println k
}

test3 = {
	println 'test3'
}

