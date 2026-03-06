using System.Reactive.Disposables;
using Microsoft.Extensions.DependencyInjection;
using Serilog;
using Serilog.Context;
using MethodBoundaryAspect.Fody.Attributes;

using Example.FodyTest;

Log.Logger = new LoggerConfiguration()
    .Enrich.FromLogContext()
    .WriteTo.Console(
        outputTemplate: "{Timestamp:yyyy-MM-dd HH:mm:ss.fff} [{Level:u3}] ({FullTypeName}) ({TypeName}.{MethodName}) {Message:lj}{NewLine}{Exception}"
    )
    .CreateLogger();

var svcs = new ServiceCollection();

svcs.AddSingleton<IItemService, ItemService>();

using var provider = svcs.BuildServiceProvider();

var service = provider.GetRequiredService<IItemService>();

service!.Create("item-A", 123);
service!.Create("item-B", 45);
try
{
    service!.InvalidOp();
}
catch(Exception)
{
    Console.WriteLine("* error");
}

var r = await service!.ExecAsync(7890);

Console.WriteLine($"* result={r}");


sealed class TraceLogAttribute : OnMethodBoundaryAspect
{
    public override void OnEntry(MethodExecutionArgs args)
    {
        var t = args.Instance.GetType();

        args.MethodExecutionTag = new CompositeDisposable([
            LogContext.PushProperty("TypeName", t?.Name),
            LogContext.PushProperty("FullTypeName", t?.FullName),
            LogContext.PushProperty("MethodName", args.Method.Name),
        ]);

        Log.Information("OnEntry");
    }

    public override void OnExit(MethodExecutionArgs args)
    {
        Log.Information("OnExit {ReturnValue}", args.ReturnValue);
        ((CompositeDisposable)args.MethodExecutionTag).Dispose();
    }

    public override void OnException(MethodExecutionArgs args)
    {
        Log.Error(args.Exception, "OnException");
        ((CompositeDisposable)args.MethodExecutionTag).Dispose();
    }
}

namespace Example.FodyTest
{
    public interface IItemService
    {
        Item Create(string name, int value);
        void InvalidOp();
        Task<string> ExecAsync(int value);
    }

    public record Item(string Id, string Name, int Value);

    class BaseService
    {
        [TraceLog]
        public void Before()
        {
            Console.WriteLine("* before");
        }
    }

    class ItemService : BaseService, IItemService
    {
        [TraceLog]
        public Item Create(string name, int value)
        {
            base.Before();

            var id = Guid.NewGuid().ToString();
            var item = new Item(id, name, value);

            return item;
        }

        [TraceLog]
        public void InvalidOp()
        {
            throw new Exception("failed");
        }

        [TraceLog]
        public async Task<string> ExecAsync(int value)
        {
            Console.WriteLine($"* exec async before: {value}");

            await Task.Delay(100);
            
            Console.WriteLine($"* exec async after: {value}");

            return "ok";
        }
    }
}