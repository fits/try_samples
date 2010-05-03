using System;
using System.Collections.Generic;
using System.Linq;
using System.Runtime.Serialization;
using System.ServiceModel;
using System.Text;

namespace TestServiceLib
{
    // メモ: ここでクラス名 "Service1" を変更する場合は、App.config で "Service1" への参照も更新する必要があります。
    [ServiceBehavior(InstanceContextMode = InstanceContextMode.Single)]
    public class TestService : ITestService
    {
        public string GetData(string value)
        {
            return string.Format("You entered: {0}", value);
        }
    }
}
