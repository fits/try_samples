using System;
using System.Collections.Generic;
using System.Drawing;
using System.Linq;
using System.Text;

namespace Fits.Sample
{
    class ImageSizeConverterApp
    {
        static void Main(string[] args)
        {
            if (args.Length < 3)
            {
                Console.WriteLine("ImageSizeConverter.exe [変換後の横幅] [入力ファイル] [出力ファイル]");
                return;
            }

            int toWidth = int.Parse(args[0]);
            var inputImg = new Bitmap(args[1]);

            double rate = (double)toWidth / inputImg.Width;

            int toHeight = (int)(inputImg.Height * rate);

            var outputImg = inputImg.GetThumbnailImage(toWidth, toHeight, () => true, new IntPtr());

            outputImg.Save(args[2]);
        }
    }
}
