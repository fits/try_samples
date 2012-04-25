
@Grab("com.google.zxing:javase:2.0")
import com.google.zxing.BarcodeFormat
import com.google.zxing.qrcode.QRCodeWriter
import com.google.zxing.client.j2se.MatrixToImageWriter

def bm = new QRCodeWriter().encode(args[0], BarcodeFormat.QR_CODE, 100, 100)
MatrixToImageWriter.writeToFile(bm, "png", new File(args[1]))
