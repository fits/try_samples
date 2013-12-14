import java.awt.Color;

import static java.nio.charset.StandardCharsets.*;
import java.nio.file.Paths;
import java.nio.file.Files;

import org.scilab.forge.jlatexmath.TeXConstants;
import org.scilab.forge.jlatexmath.TeXFormula;

public class ToPng {
	public static void main(String... args) throws Exception {

		String tex = new String(Files.readAllBytes(Paths.get(args[0])), UTF_8);
		TeXFormula formula = new TeXFormula(tex);

		formula.createPNG(TeXConstants.STYLE_DISPLAY, 20, args[1], Color.white, Color.black);
	}
}
