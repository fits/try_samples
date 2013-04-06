/**
 * Jenkins のビルドを実行する Subversion フックスクリプトのサンプル
 *
 */
@Grab('org.tmatesoft.svnkit:svnkit:1.7.8')
@Grab('org.codehaus.groovy.modules.http-builder:http-builder:0.6')
import org.tmatesoft.svn.core.*
import org.tmatesoft.svn.core.wc.*
import groovyx.net.http.*

def jenkinsUrl = 'http://localhost:8080/job/'

def repo = args[0]
def rev = args[1] as int

def manager = SVNClientManager.newInstance()
def logClient = manager.logClient

def http = new HTTPBuilder(jenkinsUrl)

def ptn = /\/trunk\/([^\/]+)\//

def logHandler = {
	it.changedPaths.collectMany { k, v ->
		def m = k =~ ptn
		(m)? [m[0][1]]: []
	}.unique().each {
		http.get(path: "${it}/build")
	}
} as ISVNLogEntryHandler

logClient.doLog(SVNURL.fromFile(new File(repo)), null, new SVNRevision(rev), new SVNRevision(rev), new SVNRevision(rev), true, true, 0, logHandler)
