import PDFDocument from 'pdfkit'
import fs from 'fs'

const doc = new PDFDocument()
doc.pipe(fs.createWriteStream('output.pdf'))

doc.fontSize(20).text('pdf create sample code', 50, 50)

doc.fillColor('red').text('red string', 100, 100)

doc.moveTo(100, 200).lineTo(120, 300).lineTo(400, 250).fill('blue')

doc.end()