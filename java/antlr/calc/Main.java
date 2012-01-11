import org.antlr.runtime.*;

public class Main {
	public static void main(String[] args) throws Exception {
		CharStream input = new ANTLRFileStream(args[0]);
		CalcLexer lexer = new CalcLexer(input);

		CommonTokenStream tokens = new CommonTokenStream(lexer);
		CalcParser parser = new CalcParser(tokens);

		parser.expr();
	}
}
