@Grab("org.functionaljava:functionaljava:3.1")
import fj.F
import static fj.data.List.*

def data = list("a1", "a2", "b1")

def f = { it as F }

def res = data.map(f { "*${it}*" })

res.foreach(f {println it})

def threeF = {
	list("${it}-1", "${it}-2", "${it}-3")
} as F

println "-------"
res.bind(threeF).bind(threeF).foreach(f {println it})

