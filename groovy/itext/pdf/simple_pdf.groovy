@Grab('com.itextpdf:itextpdf:5.3.4')
@Grab('com.itextpdf:itext-asian:5.2.0')
import com.itextpdf.text.*
import com.itextpdf.text.pdf.BaseFont
import com.itextpdf.text.pdf.PdfWriter

def doc = new Document(PageSize.A4, 50, 50, 50, 50)
PdfWriter.getInstance(doc, new FileOutputStream("sample.pdf"))

def font = new Font(
	BaseFont.createFont(
		"HeiseiKakuGo-W5",
		"UniJIS-UCS2-H", 
		BaseFont.NOT_EMBEDDED
	), 12
)

doc.addAuthor("sample user")
doc.addSubject("sample")

doc.open()

doc.add(new Paragraph("ƒTƒ“ƒvƒ‹", font))

doc.close()
