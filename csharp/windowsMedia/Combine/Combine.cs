using System;
using System.Collections.Generic;
using System.IO;
using System.Text;
using System.Threading;
using System.Runtime.InteropServices;

using WMEncoderLib;

namespace TrimAndCombine
{
    class Combine
    {
        static void Main(string[] args)
        {
			if (args.Length < 1)
			{
				Console.WriteLine("combine.exe [target dir]");
				return;
			}

            CombineParts(args[0]);

            Console.WriteLine("combine end, press enter key");

            Console.ReadLine();
        }

        /// <summary>
        /// 分割されたメディアファイルを結合する
        /// </summary>
        /// <param name="profileName"></param>
        private static void CombineParts(string dir)
        {
            WMEncoderApp app = new WMEncoderAppClass();
            IWMEncoder2 enc = app.Encoder as IWMEncoder2;

            IWMEncSourceGroupCollection sgcol = enc.SourceGroupCollection;

			WMEncProfile2 pf = new WMEncProfile2Class();
			pf.LoadFromFile(dir + "/default.prx");

            int index = 0;
            IWMEncSourceGroup2 sg2 = null;

            foreach (FileInfo fi in new DirectoryInfo(dir).GetFiles("dest*.wmv"))
            {
				Console.WriteLine("target : {0}", fi.Name);

                sg2 = sgcol.Add("sg" + index) as IWMEncSourceGroup2;
                sg2.set_Profile(pf);

				IWMEncSource asrc = sg2.AddSource(WMENC_SOURCE_TYPE.WMENC_AUDIO);
				asrc.SetInput(fi.FullName, "", "");

				IWMEncSource vsrc = sg2.AddSource(WMENC_SOURCE_TYPE.WMENC_VIDEO);
				vsrc.SetInput(fi.FullName, "", "");

                //sg2.AutoSetFileSource(fi.FullName);

                if (index > 0)
                {
                    sg2.SetAutoRollover(-1, "sg" + (index - 1));
                }

                index++;
            }

            sgcol.Active = sg2;

            FileInfo destFile = new FileInfo("default_all.wmv");
            destFile.Delete();

            enc.File.LocalFileName = destFile.FullName;

            enc.PrepareToEncode(true);
            enc.Start();

            Console.WriteLine("combine start");

            while (enc.RunState != WMENC_ENCODER_STATE.WMENC_ENCODER_STOPPED)
            {
                Thread.Sleep(2000);
            }

            Console.WriteLine("combine end : 0x{0}", enc.ErrorState.ToString("X"));

            Marshal.ReleaseComObject(pf);
            Marshal.ReleaseComObject(sgcol);
            Marshal.ReleaseComObject(enc);
            Marshal.ReleaseComObject(app);
        }
    }
}
