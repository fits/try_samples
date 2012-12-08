
import java.nio.file.*;
import java.nio.file.attribute.*;

class FindLog {
	public static void main(String[] args) throws Exception {
		final PathMatcher matcher = FileSystems.getDefault().getPathMatcher("glob:**/*_log*");

		Files.walkFileTree(Paths.get(""), new SimpleFileVisitor<Path>() {
			@Override
			public FileVisitResult visitFile(Path path, BasicFileAttributes attrs) {
				if (matcher.matches(path)) {
					System.out.println(path);
				}
				return FileVisitResult.CONTINUE;
			}
		});
	}
}
