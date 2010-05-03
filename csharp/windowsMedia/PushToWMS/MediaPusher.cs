using System;
using System.Collections.Generic;
using System.IO;
using System.Text;
using System.Threading;
using System.Runtime.InteropServices;

using WMEncoderLib;

namespace PushToWMS
{
    public class MediaPusher : IDisposable
    {
        private WMEncoderApp app;
        private WMEncoder enc;
        private IWMEncSourceGroupCollection groupList;
        private IWMEncSourceGroup group;
        private IWMEncSource source;
        private IWMEncPushDistribution pdist;

        public MediaPusher()
        {
            this.app = new WMEncoderAppClass();

            //WMEncoderAppClass から取得しなければ
            //ブロードキャストが実行できない
            this.enc = (WMEncoder)(this.app.Encoder);

            this.InitDevice();
            this.InitEncodeProfile();
            this.InitBroadCast();

            this.InitEvent();
        }

        public void Start()
        {
            this.enc.PrepareToEncode(true);

            this.enc.Start();
        }

        public void Stop()
        {
            this.enc.Flush();
            this.enc.Stop();
        }

        /// <summary>
        /// デバイスの初期設定を行う
        /// </summary>
        private void InitDevice()
        {
            this.groupList = this.enc.SourceGroupCollection;

            this.group = this.groupList.Add("sg1");
            this.source = this.group.AddSource(WMENC_SOURCE_TYPE.WMENC_VIDEO);
            this.source.SetInput("Default_Video_Device", "Device", "");
        }

        /// <summary>
        /// エンコードプロファイルの初期設定を行う
        /// </summary>
        private void InitEncodeProfile()
        {
            /*
            IWMEncProfileCollection pc = this.enc.ProfileCollection;

            foreach (IWMEncProfile2 pf in pc)
            {
                if (pf.Name.StartsWith("Windows Media Video"))
                {
                    this.group.set_Profile(pf);
                    break;
                }
            }
             */

            //自分で作成したプロファイルを使用すると
            //データ送信前後の遅延が無くなる。
            WMEncProfile2Class pf = new WMEncProfile2Class();
            pf.LoadFromFile(new FileInfo("../../test1.prx").FullName);

            this.group.set_Profile(pf);

        //    Marshal.ReleaseComObject(pc);
        }

        /// <summary>
        /// WMS に対するブロードキャストの初期設定を行う
        /// </summary>
        private void InitBroadCast()
        {
            this.pdist = (IWMEncPushDistribution)this.enc.Broadcast;

            this.pdist.AutoRemovePublishingPoint = true;
            this.pdist.ServerName = "127.0.0.1";
            this.pdist.PublishingPoint = "testpublish2";
            this.pdist.Template = "test";
        }

        /// <summary>
        /// イベント処理の初期設定を行う
        /// </summary>
        private void InitEvent()
        {
            this.enc.OnAcquireCredentials += new _IWMEncoderEvents_OnAcquireCredentialsEventHandler(enc_OnAcquireCredentials);

            this.enc.OnStateChange += new _IWMEncoderEvents_OnStateChangeEventHandler(enc_OnStateChange);

            this.enc.OnDeviceControlStateChange += new _IWMEncoderEvents_OnDeviceControlStateChangeEventHandler(enc_OnDeviceControlStateChange);

            this.enc.OnClientConnect += new _IWMEncoderEvents_OnClientConnectEventHandler(enc_OnClientConnect);

            this.enc.OnArchiveStateChange += new _IWMEncoderEvents_OnArchiveStateChangeEventHandler(enc_OnArchiveStateChange);

            this.enc.OnSourceStateChange += new _IWMEncoderEvents_OnSourceStateChangeEventHandler(enc_OnSourceStateChange);

            this.enc.OnConfigChange += new _IWMEncoderEvents_OnConfigChangeEventHandler(enc_OnConfigChange);

            this.enc.OnError += new _IWMEncoderEvents_OnErrorEventHandler(enc_OnError);
        }

        void enc_OnError(int hResult)
        {
            Console.WriteLine("error : {0}", hResult);
        }

        void enc_OnConfigChange(int hResult, string bstr)
        {
            Console.WriteLine("config change : {0}, {1}", hResult, bstr);
        }

        void enc_OnSourceStateChange(WMENC_SOURCE_STATE enumState, WMENC_SOURCE_TYPE enumType, short iIndex, string bstrSourceGroup)
        {
            Console.WriteLine("source state change : {0}, {1}, {2}, {3}", enumState, enumType, iIndex, bstrSourceGroup);
        }

        void enc_OnArchiveStateChange(WMENC_ARCHIVE_TYPE enumArchive, WMENC_ARCHIVE_STATE enumState)
        {
            Console.WriteLine("archive state change : {0}, {1}", enumArchive, enumState);
        }

        void enc_OnClientConnect(WMENC_BROADCAST_PROTOCOL protocol, string bstr)
        {
            Console.WriteLine("client connect : {0}, {1}", protocol, bstr);
        }

        /// <summary>
        /// 
        /// </summary>
        /// <param name="enumState"></param>
        /// <param name="bstrName"></param>
        /// <param name="bstrScheme"></param>
        void enc_OnDeviceControlStateChange(WMENC_DEVICECONTROL_STATE enumState, string bstrName, string bstrScheme)
        {
            Console.WriteLine("device control state change : {0}, {1}, {2}", enumState, bstrName, bstrScheme);
        }

        void enc_OnStateChange(WMENC_ENCODER_STATE enumState)
        {
            Console.WriteLine("state change : {0}", enumState);

            if (enumState == WMENC_ENCODER_STATE.WMENC_ENCODER_STOPPED)
            {
                this.enc.PrepareToEncode(false);
            }
        }

        void enc_OnAcquireCredentials(string bstrRealm, string bstrSite, ref object pvarUser, ref object pvarPassword, ref object plFlags)
        {
            Console.WriteLine("credentials : {0}, {1}, flag:{2}", bstrRealm, bstrSite, plFlags);
            pvarUser = "test";
            pvarPassword = "testpass";
        }


        #region IDisposable メンバ

        public void Dispose()
        {
            Marshal.ReleaseComObject(this.pdist);
            Marshal.ReleaseComObject(this.source);
            Marshal.ReleaseComObject(this.group);
            Marshal.ReleaseComObject(this.groupList);
            Marshal.ReleaseComObject(this.enc);
            Marshal.ReleaseComObject(this.app);
        }

        #endregion
    }
}
