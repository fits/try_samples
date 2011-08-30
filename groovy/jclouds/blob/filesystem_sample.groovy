@Grapes([
	@Grab("org.jclouds:jclouds-all:1.1.1"),
	@Grab("org.jclouds:jclouds-allblobstore:1.1.1")
])
import org.jclouds.filesystem.reference.FilesystemConstants
import org.jclouds.blobstore.BlobStoreContextFactory

def props = new Properties()
props.setProperty(FilesystemConstants.PROPERTY_BASEDIR, "./temp")

def name = "sample1"

def ctx = new BlobStoreContextFactory().createContext("filesystem", props)

def store = ctx.blobStore
store.createContainerInLocation(null, name)

def blob = store.newBlob("test")
blob.setPayload("abc")

store.putBlob(name, blob)

def b = store.getBlob(name, "test")
println "${b} - ${b.payload} - ${b.payload.rawContent}"

ctx.close()
