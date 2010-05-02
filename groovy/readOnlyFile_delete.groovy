def fname = "test.lock"

def f = new File(fname)

f.createNewFile()
f.setReadOnly()

println "can write : ${f.canWrite()}"

new File(fname).delete()
