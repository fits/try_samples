
import org.opencv.core.*
import org.opencv.imgcodecs.Imgcodecs
import org.opencv.imgproc.Imgproc

System.loadLibrary(Core.NATIVE_LIBRARY_NAME)

def src = Imgcodecs.imread(args[0], CvType.CV_8UC1)
def thr = args[1] as int

def blur = Mat.zeros(0, 0, CvType.CV_8UC1)
Imgproc.GaussianBlur(src, blur, new Size(5, 5), 0)

def input = Mat.zeros(0, 0, CvType.CV_8UC1)
Imgproc.threshold(blur, input, thr, 255, Imgproc.THRESH_BINARY_INV)

def hier = Mat.zeros(0, 0, CvType.CV_8UC1)

def contours = [] as ArrayList<MatOfPoint>

Imgproc.findContours(input, contours, hier, Imgproc.CV_RETR_EXTERNAL, Imgproc.CV_CHAIN_APPROX_NONE)

println contours

def baseName = args[0].split(/\./)[0]

Imgcodecs.imwrite("${baseName}_${thr}_binimg.jpg", input)

def res = src.clone()

Imgproc.polylines(res, contours, false, new Scalar(0))

Imgcodecs.imwrite("${baseName}_${thr}_contours.jpg", res)
