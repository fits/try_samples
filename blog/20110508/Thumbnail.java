
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.image.AffineTransformOp;
import java.io.File;
import javax.imageio.ImageIO;

/**
 * サムネイル用画像作成
 * java Thumbnail [変換後の横幅] [入力JPEGファイル] [出力JPEGファイル]
 */
public class Thumbnail {
    public static void main(String[] args) throws Exception {
        //入力JPEG読み込み
        BufferedImage input = ImageIO.read(new File(args[1]));

        //変換後の横幅
        int toWidth = Integer.parseInt(args[0]);

        double rate = (double)toWidth / input.getWidth();

        int toHeight = (int)(input.getHeight() * rate);

        //出力用イメージ
        BufferedImage output = new BufferedImage(toWidth, toHeight, input.getType());
        //サイズ変換定義
        AffineTransformOp at = new AffineTransformOp(AffineTransform.getScaleInstance(rate, rate), null);

        //サムネイル画像作成（サイズ変換）
        at.filter(input, output);

        //サムネイル画像出力
        ImageIO.write(output, "jpg", new File(args[2]));
    }
}
