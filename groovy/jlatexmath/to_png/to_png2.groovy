import java.awt.Color
import org.scilab.forge.jlatexmath.TeXConstants
import org.scilab.forge.jlatexmath.TeXFormula

def formula = new TeXFormula(new File(args[0]).getText('UTF-8'))

formula.createPNG(TeXConstants.STYLE_DISPLAY, 20, args[1], Color.white, Color.black)

// 以下でも可
//formula.createImage('png', TeXConstants.STYLE_DISPLAY, 20, args[1], Color.white, Color.black, false)
