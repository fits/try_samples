@Grab('org.mybatis:mybatis:3.2.8')
import org.apache.ibatis.session.*
import org.apache.ibatis.builder.xml.*

if (args.length < 1) {
	println '<mybatis mapper xml>'
	return
}

def config = new Configuration()

def parser = new XMLMapperBuilder(new File(args[0]).newInputStream(), config, "", config.sqlFragments)

parser.parse()

/* mappedStatements へ同一オブジェクトが 2重に登録されており、
 * 下記を回避するために collect() した後で unique() している
 *
 *   config.mappedStatements.unique() とすると
 *   UnsupportedOperationException が発生
 */
config.mappedStatements.collect().unique().each {
	println '-----'

	println "id: ${it.id}, resource: ${it.resource}, type: ${it.statementType}, cols: ${it.keyColumns}, props: ${it.keyProperties}, parameterMap: ${it.parameterMap?.type}, parameterMappings: ${it.parameterMap?.parameterMappings*.property}, resultSetType: ${it.resultSetType}, resultMaps: ${it.resultMaps*.type}"
}
