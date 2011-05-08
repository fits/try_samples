using System;
using System.Drawing;

/**
 * サムネイル用画像作成
 * Thumbnail.exe [変換後の横幅] [入力JPEGファイル] [出力JPEGファイル]
 */
class Thumbnail
{
    static void Main(string[] args)
    {
        //入力JPEG読み込み
        var inputImg = new Bitmap(args[1]);

        //変換後の横幅
        int toWidth = int.Parse(args[0]);

        double rate = (double)toWidth / inputImg.Width;

        //変換後の縦幅
        int toHeight = (int)(inputImg.Height * rate);

        //サムネイル画像作成
        var outputImg = inputImg.GetThumbnailImage(toWidth, toHeight, () => true, new IntPtr());

        //サムネイル画像出力
        outputImg.Save(args[2]);
    }
}
