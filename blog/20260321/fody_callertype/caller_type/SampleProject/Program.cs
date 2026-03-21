using SampleProject;
using CustomAttributes;

var svc = new Service();
svc.Create("check1");

TraceLogger.Info("check2");

var traceInfo = (string msg) => TraceLogger.Info(msg);
traceInfo("check3");

TraceLogger.Info2("check4");


namespace SampleProject
{
    public class TraceLogger
    {
        public static void Info(string msg, [CallerType] string callerType = "")
        {
            Console.WriteLine($"message='{msg}', caller-type='{callerType}'");
        }

        public static void Info2(string msg, [CallerType] string callerType = "", string opt = "")
        {
            Console.WriteLine($"message='{msg}', caller-type='{callerType}', opt='{opt}'");
        }
    }

    public class BaseService
    {
        protected void Before(string id)
        {
            TraceLogger.Info($"Before:{id}");
        }
    }

    public class Service : BaseService
    {
        public bool Create(String id)
        {
            base.Before(id);

            TraceLogger.Info($"Create:{id}");

            new InnerService().After(id);

            return true;
        }

        class InnerService
        {
            public void After(string id)
            {
                TraceLogger.Info($"After:{id}");
            }
        }
    }
}
