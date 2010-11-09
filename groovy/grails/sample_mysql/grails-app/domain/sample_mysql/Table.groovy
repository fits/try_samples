package sample_mysql

import java.sql.Timestamp

class Table {

    static constraints = {
    }
    
    static mapping = {
    	table "tables"
    	//id ‚Í“K“–‚Éİ’è‚µ‚Ä‚¨‚­
    	id name: "table_name"
    	version false
    }
    
    String table_schema
    String table_name
    String table_type
    String engine
    int avg_row_length
    Timestamp create_time
}
