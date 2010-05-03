using System;
using System.Collections.Generic;
using System.IO;
using System.Text;
using System.Threading;
using System.Runtime.InteropServices;

using WMEncoderLib;

namespace TrimAndCombine
{
    public class MediaTrimer : IDisposable
    {
        private int markIn;
        private int markOut;
        private string inputFile;

        private WMEncoderApp app;
        private IWMEncoder2 enc;
        private IWMEncSourceGroupCollection sgcol;
        private IWMEncProfile profile;

        public MediaTrimer(string inputFile, string profileName)
        {
            this.inputFile = inputFile;

            this.app = new WMEncoderAppClass();
            this.enc = app.Encoder as IWMEncoder2;
            this.sgcol = enc.SourceGroupCollection;

            this.profile = SelectProfile(profileName);

            if (this.profile == null)
            {
                throw new ArgumentException("nothing profile", "profileName");
            }

            this.DestFile = "default.wmv";
        }

        /// <summary>
        /// 出力ファイル名
        /// </summary>
        public string DestFile
        {
            set
            {
                this.enc.File.LocalFileName = value;
            }
        }

        public int MarkIn
        {
            set
            {
                this.markIn = value;
            }
        }

        public int MarkOut
        {
            set
            {
                this.markOut = value;
            }
        }

        /// <summary>
        /// 
        /// </summary>
        public void Start()
        {
            IWMEncSourceGroup2 sg = sgcol.Add("sg" + this.GetHashCode().ToString()) as IWMEncSourceGroup2;

            sg.AutoSetFileSource(inputFile);

            SetMark(sg);

            sg.set_Profile(this.profile);

            enc.PrepareToEncode(true);

            enc.Start();

            while (enc.RunState != WMENC_ENCODER_STATE.WMENC_ENCODER_STOPPED)
            {
                Thread.Sleep(1000);
            }

            this.sgcol.Remove(sg.Name);

            Marshal.ReleaseComObject(sg);
        }


        #region IDisposable メンバ

        public void Dispose()
        {
            Marshal.ReleaseComObject(this.sgcol);
            Marshal.ReleaseComObject(this.enc);
            Marshal.ReleaseComObject(this.app);
        }

        #endregion


        private IWMEncProfile SelectProfile(string name)
        {
            IWMEncProfile result = null;

            foreach (IWMEncProfile profile in this.enc.ProfileCollection)
            {
                if (profile.Name.StartsWith(name))
                {
                    result = profile;
                    break;
                }
            }

            return result;
        }

        private void SetMark(IWMEncSourceGroup2 sg)
        {
            foreach (WMENC_SOURCE_TYPE stype in new WMENC_SOURCE_TYPE[] { WMENC_SOURCE_TYPE.WMENC_AUDIO, WMENC_SOURCE_TYPE.WMENC_VIDEO })
            {
                if (sg.get_SourceCount(stype) > 0)
                {
                    IWMEncSource src = sg.get_Source(stype, 0);
                    src.MarkIn = this.markIn;
                    src.MarkOut = this.markOut;
                }
            }
        }
    }
}
