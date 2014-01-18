@GrabResolver('http://repository.apache.org/snapshots/')
@Grab('org.apache.commons:commons-ognl:4.0-SNAPSHOT')
import org.apache.commons.ognl.Ognl
import org.apache.commons.ognl.ClassResolver

import groovyx.gpars.*

def createData = { i ->
	def data = new Order()
	data.lines << new OrderLine('1', i)
	data.lines << new OrderLine('2', i + 1)
	data.lines << new OrderLine('3', i + 2)
	data
}

def sum = { data ->
	data.lines.inject(0){ acc, val ->
		acc + val.price
	}
}

def printResult = {
	it.groupBy().each { k, v -> println "${k} ${v.size()}" }
}

def createContext = {
	Ognl.createDefaultContext(null, { String className, Map<String, Object> context ->
		Class.forName(className)
	} as ClassResolver)
}

def ctx = createContext()

// OGNL式をコンパイル
def exprNode = Ognl.compileExpression(ctx, null, '#v = 0, lines.{ #v = #v + #this.price }, #v')

def count = 50

GParsPool.withPool(20) {
	// (1) 文字列の OGNL 式で並列処理（Context再利用）
	def res1 = (0..<count).collectParallel {
		def d = createData(it)
		try {
			sum(d) == Ognl.getValue('#v = 0, lines.{ #v = #v + #this.price }, #v', ctx, d)
		} catch (e) {
			println e
			false
		}
	}

	// (2) 文字列の OGNL 式で並列処理（Context毎回作成）
	def res2 = (0..<count).collectParallel {
		def d = createData(it)
		sum(d) == Ognl.getValue('#v = 0, lines.{ #v = #v + #this.price }, #v', createContext(), d)
	}

	// (3) compileExpression 結果で並列処理
	def res3 = (0..<count).collectParallel {
		def d = createData(it)
		sum(d) == Ognl.getValue(exprNode, d)
	}

	println '----- (1) 文字列の OGNL 式で並列処理（Context再利用）------'
	printResult res1

	println '----- (2) 文字列の OGNL 式で並列処理（Context毎回作成） ---'
	printResult res2

	println '----- (3) compileExpression 結果で並列処理 ----------------'
	printResult res3
}
