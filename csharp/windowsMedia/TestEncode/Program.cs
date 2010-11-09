using System;
using System.Collections.Generic;
using System.Text;
using System.Threading;

using WMEncoderLib;

namespace TestEncode
{
    class Program
    {
        static void Main(string[] args)
        {
            if (args.Length < 1)
            {
                Console.WriteLine(">TestEncode [wmv file]");
                return;
            }

            WMEncBasicEdit editor = new WMEncBasicEditClass();
            editor.MediaFile = args[0];
            editor.OutputFile = "test.wmv";
            editor.ConfigFile = "../../config.txt";

            editor.Start();

            Console.ReadLine();
        }
    }
}
