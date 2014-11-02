
import com.ibatis.sqlmap.engine.builder.xml.*

import groovy.json.JsonSlurper

if (args.length < 1) {
	println '<ibatis mapper xml>'
	return
}

def state = new XmlParserState()

def parser = new SqlMapParser(state)

parser.parse(new File(args[0]).newInputStream())

// SqlMapExecutorDelegate
def dlg = state.config.delegate

dlg.mappedStatementNames.collect { dlg.getMappedStatement it }.each {
	println '-----'
	println "id: ${it.id}, resultSetType: ${it.resultSetType}, resultMap: ${it.resultMap?.resultClass}, parameterMap: ${it.parameterMap?.parameterMappings*.propertyName}, parameterClass: ${it.parameterClass}, resource: ${it.resource}"
}
