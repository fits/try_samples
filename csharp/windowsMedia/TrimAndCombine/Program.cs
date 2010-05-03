using System;
using System.Collections.Generic;
using System.IO;
using System.Text;
using System.Threading;
using System.Runtime.InteropServices;

using WMEncoderLib;

namespace TrimAndCombine
{
    class Program
    {
        static void Main(string[] args)
        {
            int num = (args.Length > 0) ? int.Parse(args[0]) : 3;

            FileInfo inputFile = new FileInfo("../../default.wmv");
            string profileName = "Windows Media Video";

            int duration = GetDuration(inputFile);

            MultiTrim(inputFile.FullName, profileName, duration, num);

            Console.WriteLine("trim end");

            CombineParts(profileName);

            Console.WriteLine("combine end, press enter key");

            Console.ReadLine();
        }

        /// <summary>
        /// 分割されたメディアファイルを結合する
        /// </summary>
        /// <param name="profileName"></param>
        private static void CombineParts(string profileName)
        {
            WMEncoderApp app = new WMEncoderAppClass();
            IWMEncoder2 enc = app.Encoder as IWMEncoder2;

            IWMEncSourceGroupCollection sgcol = enc.SourceGroupCollection;

            IWMEncProfile profile = SelectProfile(enc, profileName);
            int index = 0;
            IWMEncSourceGroup2 sg2 = null;

            foreach (FileInfo fi in new DirectoryInfo(".").GetFiles("dest*.wmv"))
            {
                sg2 = sgcol.Add("sg" + index) as IWMEncSourceGroup2;
                sg2.set_Profile(profile);
                sg2.AutoSetFileSource(fi.FullName);

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

            Console.WriteLine("combine end");

            Marshal.ReleaseComObject(profile);
            Marshal.ReleaseComObject(sgcol);
            Marshal.ReleaseComObject(enc);
            Marshal.ReleaseComObject(app);
        }

        /// <summary>
        /// マルチスレッドで分割処理を実施する
        /// </summary>
        /// <param name="inputFile"></param>
        /// <param name="duration"></param>
        /// <param name="num"></param>
        private static void MultiTrim(string inputFile, string profileName, int duration, int num)
        {
            List<Thread> tlist = new List<Thread>(num);

            for (int i = 0; i < num; i++)
            {
                MediaTrimer trim = new MediaTrimer(inputFile, profileName);
                trim.MarkIn = (duration * i) / num;
                trim.MarkOut = (duration * (i + 1) / num) - (duration / num / 5);
                trim.DestFile = new FileInfo(string.Format("dest{0}.wmv", i)).FullName;

                Thread th = new Thread(delegate()
                {
                    Console.WriteLine("encode start : {0}", trim.GetHashCode());

                    try
                    {
                        trim.Start();

                        Console.WriteLine("encode stop : {0}", trim.GetHashCode());
                    }
                    catch (Exception ex)
                    {
                        Console.WriteLine(ex.StackTrace);
                    }
                    finally
                    {
                        trim.Dispose();
                    }
                });

                tlist.Add(th);
            }

            foreach (Thread th in tlist)
            {
                th.Start();
            }

            foreach (Thread th in tlist)
            {
                th.Join();
            }
        }

        /// <summary>
        /// 順番に分割処理を実施する
        /// </summary>
        /// <param name="inputFile"></param>
        /// <param name="profileName"></param>
        /// <param name="duration"></param>
        /// <param name="num"></param>
        private static void OrderTrim(string inputFile, string profileName, int duration, int num)
        {
            WMEncoderApp app = new WMEncoderAppClass();
            IWMEncoder2 enc = app.Encoder as IWMEncoder2;

            IWMEncSourceGroupCollection sgcol = enc.SourceGroupCollection;

            IWMEncProfile profile = SelectProfile(enc, profileName);

            for (int i = 0; i < num; i++)
            {
                if (sgcol.Count > 0)
                {
                    sgcol.Remove("sg1");
                }

                IWMEncSourceGroup2 sg = sgcol.Add("sg1") as IWMEncSourceGroup2;
                sg.AutoSetFileSource(inputFile);

                int start = (duration * i) / num;
                int end = (duration * (i + 1) / num) - 30000;

                SetMark(sg, start, end);

                sg.set_Profile(profile);

                enc.File.LocalFileName = new FileInfo(string.Format("dest{0}.wmv", i)).FullName;
                enc.PrepareToEncode(true);

                enc.Start();

                Console.WriteLine("encode start : {0} - start:{1}, end:{2}", i, start, end);

                while (enc.RunState != WMENC_ENCODER_STATE.WMENC_ENCODER_STOPPED)
                {
                    Thread.Sleep(2000);
                }

                Console.WriteLine("encode end : {0}", i);
            }

            Marshal.ReleaseComObject(profile);
            Marshal.ReleaseComObject(sgcol);
            Marshal.ReleaseComObject(enc);
            Marshal.ReleaseComObject(app);
        }

        private static void SetMark(IWMEncSourceGroup2 sg, int start, int end)
        {
            foreach (WMENC_SOURCE_TYPE stype in new WMENC_SOURCE_TYPE[] { WMENC_SOURCE_TYPE.WMENC_AUDIO, WMENC_SOURCE_TYPE.WMENC_VIDEO})
            {
                if (sg.get_SourceCount(stype) > 0)
                {
                    IWMEncSource src = sg.get_Source(stype, 0);
                    src.MarkIn = start;
                    src.MarkOut = end;
                }
            }
        }

        private static IWMEncProfile SelectProfile(IWMEncoder enc, string name)
        {
            IWMEncProfile result = null;

            foreach (IWMEncProfile profile in enc.ProfileCollection)
            {
                if (profile.Name.StartsWith(name))
                {
                    result = profile;
                    break;
                }
            }
            return result;
        }

        private static int GetDuration(FileInfo file)
        {
            WMEncBasicEdit edit = new WMEncBasicEditClass();
            edit.MediaFile = file.FullName;

            int result = (int)edit.Duration;

            Marshal.ReleaseComObject(edit);

            return result;
        }
    }
}
