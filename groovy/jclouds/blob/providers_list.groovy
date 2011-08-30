@Grapes([
	@Grab("org.jclouds:jclouds-all:1.1.1"),
	@Grab("org.jclouds:jclouds-allblobstore:1.1.1")
])
import org.jclouds.blobstore.util.BlobStoreUtils

BlobStoreUtils.supportedProviders.each {
	println it
}
