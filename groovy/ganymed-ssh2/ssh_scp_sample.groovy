
@Grab('ch.ethz.ganymed:ganymed-ssh2:262')
import ch.ethz.ssh2.*
import static ch.ethz.ssh2.ChannelCondition.*

/*
 * 踏み台サーバー経由でターゲットのホストからファイルを scp するサンプル
 *
 * -> proxy host -> target host
 *
 *
 */
if (args.length < 2) {
	println "<target host> <File>"
	return
}

def props = new Properties()
props.load(new File('setting.properties').newReader())

def host = args[0]
def file = args[1]

def con = new Connection(props.proxy_host)
con.connect()

if (con.authenticateWithPassword(props.proxy_user, props.proxy_pass)) {
	try {
		def session = con.openSession()

		def tempFilePath = "${props.work_dir}/${host}_${file}"

		session.execCommand "sudo scp root@${host}:${props.target_dir}/${file} ${tempFilePath}"

		// コマンドの終了を待機
		def res = session.waitForCondition(STDOUT_DATA | STDERR_DATA | EOF, props.timeout as long)

		println "wait result : ${res}"

		def destDir = props.dest_dir

		new File(destDir).mkdir()

		def scp = con.createSCPClient()
		scp.get(tempFilePath, destDir)

		println "copied : ${tempFilePath}"

		session.close()
	} finally {
		con.close()
	}
}
