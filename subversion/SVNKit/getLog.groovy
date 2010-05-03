/**
* "svn log ファイル名" と同等の処理を実行するサンプル
*
* （注）
*    カレントディレクトリのファイルを指定する場合は
*    "./ファイル名" と指定する点に注意
*    （そうしないとワーキングコピーでは無いというエラーがでる）
*/

import org.tmatesoft.svn.core.ISVNLogEntryHandler
import org.tmatesoft.svn.core.internal.io.fs.FSRepositoryFactory
import org.tmatesoft.svn.core.wc.SVNClientManager
import org.tmatesoft.svn.core.wc.SVNRevision

//file:// のリポジトリを使用する場合に必要
FSRepositoryFactory.setup()

def manager = SVNClientManager.newInstance()
def logClient = manager.logClient

def logHandler = {logEntry -> println logEntry} as ISVNLogEntryHandler

logClient.doLog([new File(args[0])] as File[], new SVNRevision(0), SVNRevision.HEAD, true, false, 0, logHandler)

