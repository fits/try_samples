using TestProject;

var svc = new Service();
svc.Create("test-1");

LogUtil.Info("main process");

namespace TestProject
{
    public class LogUtil
    {
        public static void Info(string msg, string callerType = "")
        {
            Console.WriteLine($"message='{msg}', caller-type='{callerType}'");
        }
    }

    public class BaseService
    {
        protected void Before()
        {
            LogUtil.Info("called before");
        }
    }

    public class Service : BaseService
    {
        public bool Create(String name)
        {
            base.Before();

            LogUtil.Info($"created: {name}");

            return true;
        }
    }
}
