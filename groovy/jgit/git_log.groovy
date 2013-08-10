@Grab('org.eclipse.jgit:org.eclipse.jgit:3.0.0.201306101825-r')
import org.eclipse.jgit.api.Git
import org.eclipse.jgit.internal.storage.file.FileRepository

def git = new Git(new FileRepository(args[0]))

git.log().setMaxCount(5).call().each {
	println "id: ${it.id}"
	println "author: ${it.authorIdent}"
	println "committer: ${it.committerIdent}"

	println "message: ${it.shortMessage}"
	println "type: ${it.type}"
	println "time: ${it.commitTime}"
	println "-----------------------"
}
