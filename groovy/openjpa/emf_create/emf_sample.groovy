@Grab('org.apache.openjpa:openjpa:2.4.0')
@Grab('mysql:mysql-connector-java:5.1.36')
import javax.persistence.Persistence

def conf = new Properties()
conf.load(new FileInputStream('db.properties'))

def emf = Persistence.createEntityManagerFactory('openjpa', conf)

def em = emf.createEntityManager()

println em

em.close()
