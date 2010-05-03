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
}
