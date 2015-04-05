import org.atilika.kuromoji.Tokenizer;

public class KuromojiSample {
	public static void main(String... args) {

		Tokenizer.builder().build().tokenize(args[0]).forEach(t -> 
			System.out.println(t.getSurfaceForm()));

	}
}
