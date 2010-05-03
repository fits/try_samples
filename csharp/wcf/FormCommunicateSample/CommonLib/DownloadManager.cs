using System;
using System.ServiceModel;

namespace CommonLib
{
    public delegate string CopyFileDelegate(string file);

    [ServiceBehavior(InstanceContextMode = InstanceContextMode.Single)]
    public class DownloadManager : IDownloadManager
    {
        public CopyFileDelegate CopyFileCallBack { get; set; }

        #region IDownloadManager メンバ

        public string CopyFile(string file)
        {
            return this.CopyFileCallBack(file);
        }

        #endregion
    }
}
