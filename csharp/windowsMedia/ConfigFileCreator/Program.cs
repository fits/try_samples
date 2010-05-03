using System;
using System.Collections.Generic;
using System.Text;

using WMEncoderLib;

namespace ConfigFileCreator
{
    class Program
    {
        static void Main(string[] args)
        {
            if (args.Length < 1)
            {
                Console.WriteLine(">configFileCreator [wmv file]");
                return;
            }

            WMEncBasicEdit editor = new WMEncBasicEditClass();
            editor.MediaFile = args[0];
            editor.SaveConfigFile("config.txt");
        }
    }
}
