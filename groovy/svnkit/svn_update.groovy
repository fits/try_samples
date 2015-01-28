@Grab('org.tmatesoft.svnkit:svnkit:1.8.7')
import org.tmatesoft.svn.core.SVNDepth
import org.tmatesoft.svn.core.wc.SVNRevision
import org.tmatesoft.svn.core.wc.SVNClientManager

def manager = SVNClientManager.newInstance()

def client = manager.updateClient

def res = client.doUpdate(new File(args[0]), SVNRevision.HEAD, SVNDepth.INFINITY, true, false)

println "revision: ${res}"
