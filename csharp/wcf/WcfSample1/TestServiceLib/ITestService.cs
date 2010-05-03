using System;
using System.Collections.Generic;
using System.Linq;
using System.Runtime.Serialization;
using System.ServiceModel;
using System.Text;

namespace TestServiceLib
{
    // メモ: ここでインターフェイス名 "IService1" を変更する場合は、App.config で "IService1" への参照も更新する必要があります。
    [ServiceContract]
    public interface ITestService
    {
        [OperationContract]
        string GetData(string value);

        // タスク: ここにサービス操作を追加します。
    }

}
