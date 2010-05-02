
import voldemort.client.*
import voldemort.versioning.Versioned

def factory = new SocketStoreClientFactory(new ClientConfig().setBootstrapUrls("tcp://localhost:6666"))

def client = factory.getStoreClient("test")

def k = "a1"

def v = client.get(k)

if (v == null) {
	client.put(k, new Versioned("テストデータ"))
	v = client.get(k)
}

println v

v.setObject(v.value + "_next")

client.put(k, v)
