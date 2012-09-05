
@Grab('org.apache.commons:commons-vfs2:2.0')
@Grab('commons-httpclient:commons-httpclient:3.1')
import org.apache.commons.vfs2.*
import org.apache.commons.vfs2.auth.StaticUserAuthenticator
import org.apache.commons.vfs2.impl.DefaultFileSystemConfigBuilder

if (args.length < 3) {
	println "groovy listup_vfs.groovy <url> <user> <password>"
	return
}

def url = args[0]
def user = args[1]
def pass = args[2]

def fs = VFS.manager

def auth = new StaticUserAuthenticator(null, user, pass)
def opts = new FileSystemOptions()
DefaultFileSystemConfigBuilder.instance.setUserAuthenticator(opts, auth)

def fo = fs.resolveFile(url, opts)

println "name = $fo, type = $fo.type"

fo.close()
