@GrabResolver(name = 'sardine-google-svn-repo', root = 'http://sardine.googlecode.com/svn/maven')
@Grab('com.googlecode.sardine:sardine:314')
@Grab('org.slf4j:slf4j-nop:1.6.6')
/*
 * 以下のエラーが発生するため
 *   download failed ・・・httpclient;4.1.1!httpclient.jar,
 *   download failed ・・・commons-codec;1.4!commons-codec.jar
 *
 *  .groovy/grapes/com.googlecode.sardine/sardine/ivy-314.xml を
 *  以下のように編集
 *
 *    (1) httpclient と httpcore のバージョンを 4.2.1 に変更
 *    (2) commons-codec のバージョンを 1.6 に変更
 *
 */
import com.googlecode.sardine.*

if (args.length < 4) {
	println "groovy fileupload_sardine.groovy <url> <user> <password> <file>"
	return
}

def baseUrl = args[0]
def user = args[1]
def pass = args[2]
def file = new File(args[3])

def sar = SardineFactory.begin(user, pass)

if (!sar.exists(baseUrl)) {
	// 親ディレクトリが存在しないと作成に失敗する
	sar.createDirectory(baseUrl)
}

sar.put("$baseUrl/${file.name}", file.bytes)
