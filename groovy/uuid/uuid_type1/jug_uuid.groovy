@Grapes([
	@Grab('com.fasterxml.uuid:java-uuid-generator:3.1.3'),
	@GrabExclude('log4j#log4j;1.2.13')
])
import com.fasterxml.uuid.Generators

// タイプ1 の UUID を生成する Generator
def generator = Generators.timeBasedGenerator()

(1..5).each {
	// タイプ1 の UUID を生成
	println generator.generate()
}
