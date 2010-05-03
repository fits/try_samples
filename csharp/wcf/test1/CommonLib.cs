using System;
using System.ServiceModel;

namespace CommonLib
{
    [ServiceContract]
    public interface IDownloadManager
    {
        [OperationContract]
        string CopyFile(string file);
    }

    public delegate string CopyFileDelegate(string file);

    [ServiceBehavior(InstanceContextMode = InstanceContextMode.Single)]
    public class DownloadManager : IDownloadManager
    {
		private CopyFileDelegate cf;

        public CopyFileDelegate CopyFileCallBack {
			get
			{
				return this.cf;
			}
			set
			{
				this.cf = value;
			}
		}

        public string CopyFile(string file)
        {
            return this.CopyFileCallBack(file);
        }
    }
}
