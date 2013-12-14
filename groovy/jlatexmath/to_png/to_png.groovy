
import java.awt.Color
import java.awt.Graphics2D
import java.awt.Insets
import java.awt.image.BufferedImage

import javax.imageio.ImageIO
import javax.swing.Icon
import javax.swing.JLabel

import org.scilab.forge.jlatexmath.TeXConstants
import org.scilab.forge.jlatexmath.TeXFormula
import org.scilab.forge.jlatexmath.TeXIcon

def formula = new TeXFormula(new File(args[0]).getText('UTF-8'))

def icon = formula.createTeXIcon(TeXConstants.STYLE_DISPLAY, 20)
icon.insets = new Insets(5, 5, 5, 5)

def img = new BufferedImage(icon.iconWidth, icon.iconHeight, BufferedImage.TYPE_INT_ARGB)

def g2 = img.createGraphics()
g2.color = Color.white
g2.fillRect(0, 0, icon.iconWidth, icon.iconHeight)

def label = new JLabel()
label.foreground = Color.black

icon.paintIcon(label, g2, 0, 0)

ImageIO.write(img, 'png', new File(args[1]))
