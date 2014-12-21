@Grab('org.apache.ibatis:ibatis-sqlmap:2.3.4.726')
import com.ibatis.sqlmap.engine.builder.xml.*
import com.ibatis.sqlmap.engine.scope.*

import groovy.json.JsonSlurper

if (args.length < 3) {
	println '<ibatis mapper xml> <sql id> <json params>'
	return
}

def state = new XmlParserState()

def parser = new SqlMapParser(state)
parser.parse(new File(args[0]).newInputStream())

// get SqlMapExecutorDelegate
def dlg = state.config.delegate

def st = dlg.getMappedStatement(args[1])
def sql = st.sql

def scope = new StatementScope(new SessionScope())
scope.statement = st

// json -> object
def params = new JsonSlurper().parseText args[2]

println sql.getSql(scope, params)
