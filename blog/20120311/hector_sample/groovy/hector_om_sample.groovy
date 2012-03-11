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
@Table(name = "Order")
class Order {
	@Id
	String id

	@Column(name = "user_id")
	String userId

	@me.prettyprint.hom.annotations.Column(name = "lines")
	List<OrderLine> lines
}

class OrderLine implements Serializable {
	String productId
	int quantity
}


def cluster = HFactory.getOrCreateCluster("Test Cluster", "localhost:9160")
def keyspace = HFactory.createKeyspace('Sample', cluster)

def cacheMgr = new ClassCacheMgr()
cacheMgr.initializeCacheForClass(Order)

def em = new EntityManagerImpl(keyspace, null, cacheMgr, null)

def data = new Order(id: "id1", userId: "U1", lines: [
	new OrderLine(productId: "P1", quantity: 1),
	new OrderLine(productId: "P2", quantity: 2)
])

em.persist(data)


def res = em.find(Order, "id1")
println res.dump()

res.lines.each {
	println "${it.productId}, ${it.quantity}"
}

