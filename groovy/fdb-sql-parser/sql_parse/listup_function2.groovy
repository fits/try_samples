@Grab('com.foundationdb:fdb-sql-parser:1.4.0')
import com.foundationdb.sql.parser.*

def findNodes = { node, trgClass ->
	def res = []

	node.accept(
		[
			visit: { n -> 
				if (trgClass.isInstance(n)) {
					res << n
				}
				n
			},
			skipChildren: { n -> false },
			stopTraversal: { -> false },
			visitChildrenFirst: { n -> false}
		] as Visitor
	)
	res
}

def paramToStr = { p ->
	def operatorNodeProc = { n -> "( ${owner.call(n.leftOperand)} ) ${n.operator} ( ${owner.call(n.rightOperand)} )" }

	[
		[ ConstantNode, { n -> n.value } ],
		[ ParameterNode, { n -> '?' } ],
		[ UnaryOperatorNode, { n -> n.operator } ],
		[ ColumnReference, { n -> n.columnName } ],
		[ BinaryOperatorNode, operatorNodeProc ],
		[ TernaryOperatorNode, operatorNodeProc ]

	].inject(null) { acc, v ->
		if (acc == null && v.first().isInstance(p)) {
			acc = v.last().call(p)
		}
		acc
	}
}

def node = new SQLParser().parseStatement(System.in.text)

findNodes(node, JavaToSQLValueNode)*.javaValueNode.findAll {
	it instanceof MethodCallNode
}.collect {
	def params = it.methodParameters.collect { it.getSQLValueNode() }.collect {
		paramToStr it
	}.join(', ')

	"${it.methodName} ($params)"
}.unique().each { println it }

// built-in ternary operators (ex. substring)
findNodes(node, TernaryOperatorNode).findAll {
	// exclude 'like'
	!(it instanceof LikeEscapeOperatorNode)
}.collect {
	paramToStr it
}.unique().each { println it }
