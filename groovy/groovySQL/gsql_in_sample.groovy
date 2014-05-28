import groovy.sql.*

def createDb = { info ->
	Sql.newInstance(
		info.db_url,
		info.db_user,
		info.db_pass,
		info.db_driver
	)
}

def search = { db, category, codeList ->
	def sql = """
		select
			*
		from
			sample
		where
			category = ? and
			code in ( ${(['?'] * codeList.size()).join(',')} )
	"""

	db.rows(sql, [category, codeList].flatten())
}

def category = args[0]

def setting = new Properties()
setting.load(new File(configFile).newInputStream())

def searchDb = search.curry(createDb(setting), category)

new File(args[1]).readLines().collate(10).each {
	println '-----'
	println searchDb(it*.code)
}
