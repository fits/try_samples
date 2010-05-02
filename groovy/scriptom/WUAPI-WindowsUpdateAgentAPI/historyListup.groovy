
import org.codehaus.groovy.scriptom.*

Scriptom.inApartment {
	def session = new ActiveXObject("Microsoft.Update.Session")
	def searcher = session.CreateUpdateSearcher()

	def historyCount = searcher.GetTotalHistoryCount()

	println "Windows Update History Count: $historyCount"

	def histories = searcher.QueryHistory(0, historyCount)

	histories.each {
		println "${it.Title} = ${it.ResultCode}"
	}
}
