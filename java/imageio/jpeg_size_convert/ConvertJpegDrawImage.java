
import java.awt.image.*;
import java.io.*;
import javax.imageio.*;

//JPEGのサイズ変換（drawImage版）
public class ConvertJpegDrawImage {

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
		output.getGraphics().drawImage(input, 0, 0, width2, height2, null);

		//JPEG出力
		ImageIO.write(output, "jpg", new File(args[2]));
	}
}

