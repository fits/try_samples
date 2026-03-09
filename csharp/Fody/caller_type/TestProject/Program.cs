using TestProject;
using CustomAttributes;

var svc = new Service();
svc.Create("test-1");

Logger.Info("main process");

var InfoFunc = (string msg) => Logger.Info(msg);

InfoFunc("from lambda function");

Logger.Info2("main process2");


namespace TestProject
{
    public class Logger
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
        protected void Before()
        {
            Logger.Info("called before");
        }
    }

    public class Service : BaseService
    {
        public bool Create(String name)
        {
            base.Before();

            Logger.Info($"created: {name}");

            new InnerService().Call();

            return true;
        }

        class InnerService
        {
            public void Call()
            {
                Logger.Info($"inner call: {this}");
            }
        }
    }
}
