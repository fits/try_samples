package sample_mysql

import grails.converters.*

class InformationsController {

    def index = { }

    def databases = {
    	def c = Table.createCriteria()
    	def result = c.list {
    		projections {
    			distinct("table_schema")
    		}
    		order("table_schema")
    	}
    	
    	result = result.collect {
    		[table_schema: it]
    	}

    	render result as JSON
    }

    def tables = {
    	def result = Table.findAllByTable_schema(params.id)
    	render result as JSON
    }
}
