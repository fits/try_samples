@Grab('org.mybatis:mybatis:3.2.8')
import org.apache.ibatis.session.*
import org.apache.ibatis.builder.xml.*

import groovy.json.JsonSlurper

if (args.length < 3) {
	println '<mybatis mapper xml> <sql id> <json params>'
	return
}

def config = new Configuration()

def parser = new XMLMapperBuilder(new File(args[0]).newInputStream(), config, "", config.sqlFragments)

parser.parse()

def st = config.getMappedStatement(args[1])

def params = new JsonSlurper().parseText args[2]

def sql = st.getBoundSql(params).sql

println sql
