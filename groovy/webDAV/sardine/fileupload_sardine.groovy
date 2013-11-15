@Grab('com.github.lookfirst:sardine:5.0.1')
@Grab('org.slf4j:slf4j-nop:1.7.5')
import com.github.sardine.*

if (args.length < 4) {
	println "groovy fileupload_sardine.groovy <user> <password> <url> <file>"
	return
}

def user = args[0]
def pass = args[1]
def baseUrl = args[2]
def file = new File(args[3])

def sar = SardineFactory.begin(user, pass)

if (!sar.exists(baseUrl)) {
	// 親ディレクトリが存在しないと作成に失敗する
	sar.createDirectory(baseUrl)
}

sar.put("$baseUrl/${file.name}", file.bytes)
