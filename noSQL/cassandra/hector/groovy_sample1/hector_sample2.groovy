package fits.sample

@Grapes([
	@Grab('me.prettyprint:hector-object-mapper:3.0-02'),
	@GrabExclude('commons-pool#commons-pool')
])
@Grab('org.slf4j:slf4j-jdk14:1.6.4')
import javax.persistence.*
import me.prettyprint.hector.api.factory.HFactory
import me.prettyprint.hom.EntityManagerImpl
import me.prettyprint.hom.ClassCacheMgr

@Entity
@Table(name = "Book")
class Book {
	@Id
	String id

	@Column(name = "name")
	String name
}

def cluster = HFactory.getOrCreateCluster("Test Cluster", "localhost:9160")
def keyspace = HFactory.createKeyspace('Sample', cluster)

def cacheMgr = new ClassCacheMgr()
cacheMgr.initializeCacheForClass(Book)

def em = new EntityManagerImpl(keyspace, null, cacheMgr, null)

def data = new Book(id: "id1", name: "test")

em.persist(data)

