import org.opencv.core.*;
import org.opencv.highgui.Highgui;

public class ImageDataDump {
	static {
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
	}

	public static void main(String[] args) {
		if (args.length < 1) {
			System.out.println("java ImageDataDump <image file>");
			return;
		}

		Mat img = Highgui.imread(args[0]);

		System.out.println("rows = " + img.rows() + ", cols = " + img.cols());

		for (int r = 0; r < img.rows(); r++) {
			for (int c = 0; c < img.cols(); c++) {
				double[] data = img.get(r, c);
				System.out.println("(" + data[2] + ", " + data[1] + ", " + data[0] + ")");
			}
		}
	}
}