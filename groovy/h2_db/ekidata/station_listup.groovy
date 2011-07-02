@Grapes([
    @Grab(group = 'com.h2database', module = 'h2', version = '1.3.157'),
    @GrabConfig(systemClassLoader = true)
])
import groovy.sql.Sql

def db = Sql.newInstance("jdbc:h2:mem:", "org.h2.Driver")

def sql = """
SELECT
 *
FROM (
	SELECT
		pref_name,
		station_g_cd,
		station_name,
		count(*) as lines
	FROM
	  CSVREAD('m_station.csv') S
	  JOIN CSVREAD('m_pref.csv') P
	    ON S.pref_cd=P.pref_cd
	GROUP BY station_g_cd, station_name
	ORDER BY lines DESC
)
WHERE ROWNUM <= 10
"""

db.eachRow(sql) {r ->
    println "${r.station_name}âwÅi${r.pref_name}Åj: ${r.lines}"
}


