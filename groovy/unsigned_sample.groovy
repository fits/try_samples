
int a = -100

// -100
long b = a as long
println b

// 4294967196
println Integer.toUnsignedLong(a)

// 65436
def c = a & 0xffff
println c

// 4294967196
def d = a & 0xffffffff
println d

// 2147483647
println Integer.MAX_VALUE

// Long
def e = 0xffffffff
println e.class

// Integer
def f = 0xfffffff
println f.class
