
import java.awt.image.*;
import java.awt.geom.*;
import java.io.*;
import javax.imageio.*;

//JPEGのサイズ変換（AffineTransform版）
// drawImage よりも少し処理が高速な模様
public class ConvertJpegAffineTrans {

	public static void main(String[] args) throws Exception {
		if (args.length < 3) {
			System.out.println("java ConvertJpegDrawImage [変換後の横幅] [入力JPEGファイル] [出力JPEGファイル]");
			return;
		}

		//入力JPEG読み込み
		BufferedImage input = ImageIO.read(new File(args[1]));

		//変換後の横幅
		int toWidth = Integer.parseInt(args[0]);

		double rate = (double)toWidth / input.getWidth();

		int width2 = (int)(input.getWidth() * rate);
		int height2 = (int)(input.getHeight() * rate);

		BufferedImage output = new BufferedImage(width2, height2, input.getType());
		//サイズ変換の定義
		AffineTransformOp at = new AffineTransformOp(AffineTransform.getScaleInstance(rate, rate), null);
	//	AffineTransformOp at = new AffineTransformOp(AffineTransform.getScaleInstance(rate, rate), AffineTransformOp.TYPE_BICUBIC);

		//変換
		at.filter(input, output);

		//JPEG出力
		ImageIO.write(output, "jpg", new File(args[2]));
	}
}

