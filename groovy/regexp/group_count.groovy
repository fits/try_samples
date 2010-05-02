
def str = "at\t\t\t\t\t"

def m = str =~ /\t/

//count ‚Í Groovy ‚ÌŠg’£‚Å Java ‚É‚Í groupCount() ‚µ‚©‚È‚¢
println "count : ${m.count}, group count: ${m.groupCount()}"

while(m.find()) {
	println "$m"
}
