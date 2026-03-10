using Anotar.Serilog;
using Serilog;
using TestProject;

Log.Logger = new LoggerConfiguration()
    .WriteTo.Console(
        outputTemplate: "{Timestamp:yyyy-MM-dd HH:mm:ss.fff} [{Level:u3}] ({SourceContext}.{MethodName}:{LineNumber}) {Message:lj}{NewLine}{Exception}"
    )
    .CreateLogger();

var svc = new Service();
svc.Create("test-1");

LogTo.Information("main process");

var InfoFunc = (string msg) => LogTo.Information(msg);

InfoFunc("from lambda function");


namespace TestProject
{
    public class BaseService
    {
        protected void Before()
        {
            LogTo.Information("called before");
        }
    }

    public class Service : BaseService
    {
        public bool Create(String name)
        {
            base.Before();

            LogTo.Information($"created: {name}");

            new InnerService().Call();

            return true;
        }

        class InnerService
        {
            public void Call()
            {
                LogTo.Information($"inner call: {this}");
            }
        }
    }
}
