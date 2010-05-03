
import org.tmatesoft.svn.core.ISVNLogEntryHandler
import org.tmatesoft.svn.core.SVNDepth
import org.tmatesoft.svn.core.internal.io.fs.FSRepositoryFactory
import org.tmatesoft.svn.core.wc.SVNClientManager
import org.tmatesoft.svn.core.wc.SVNRevision
import org.tmatesoft.svn.core.wc.ISVNDiffStatusHandler


//file:// のリポジトリを使用する場合に必要
FSRepositoryFactory.setup()

def manager = SVNClientManager.newInstance()

def getFirstRevision = {fileName ->
	def result = 0

	manager.logClient.doLog([new File(fileName)] as File[], new SVNRevision(0), SVNRevision.HEAD, true, false, 1, {logEntry -> 
		result = logEntry.revision
	} as ISVNLogEntryHandler)

	new SVNRevision(result)
}

def fileName = args[0]

def firstRev = getFirstRevision(fileName)
println "first revision : ${firstRev}"

//diff
manager.diffClient.doDiff([new File(fileName)] as File[], firstRev, SVNRevision.HEAD, SVNRevision.UNDEFINED, SVNDepth.EMPTY, true, System.out, null)

println "--------------------"

//diff status
manager.diffClient.doDiffStatus(new File(fileName), firstRev, SVNRevision.HEAD, SVNRevision.UNDEFINED, SVNDepth.EMPTY, true, {status ->
	println "${status.kind}, ${status.modificationType} : ${status.path}, ${status.uRL}"
} as ISVNDiffStatusHandler)

