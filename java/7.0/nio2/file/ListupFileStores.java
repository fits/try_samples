
import java.nio.file.*;

public class ListupFileStores {
	public static void main(String[] args) throws Exception {

		FileSystem fs = FileSystems.getDefault();

		System.out.println("--- FileAttributeViews ---");
		for (String attr : fs.supportedFileAttributeViews()) {
			System.out.println(attr);
		}

		System.out.println("--- FileStore ---");
		for (FileStore fstore : fs.getFileStores()) {
			System.out.printf("%s - %s, %d \n", fstore.name(), fstore.type(), fstore.getTotalSpace());
		}
	}
}