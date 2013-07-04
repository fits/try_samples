@Grab('org.apache.pdfbox:pdfbox:1.8.2')
@Grab('org.bouncycastle:bcprov-jdk15on:1.49')
import org.apache.pdfbox.pdmodel.*
import org.apache.pdfbox.util.PDFTextStripper

def doc = PDDocument.load(args[0])

// PDF ‚Ì“à—e‚É‚æ‚Á‚Ä‚Í“ú–{Œê‚ª•¶Žš‰»‚¯‚·‚é
println new PDFTextStripper().getText(doc)

doc.close()
