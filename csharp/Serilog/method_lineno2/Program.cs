using System.Diagnostics;
using Microsoft.Extensions.DependencyInjection;
using Serilog;
using Serilog.Context;

using Example.CallerTest;

Log.Logger = new LoggerConfiguration()
    .Enrich.FromLogContext()
    .WriteTo.Console(
        outputTemplate: "{Timestamp:yyyy-MM-dd HH:mm:ss.fff} [{Level:u3}] ({FullTypeName}) ({TypeName}.{MethodName} {FileName}:{LineNumber}) {Message:lj}{NewLine}{Exception}"
    )
    .CreateLogger();

var svcs = new ServiceCollection();

svcs.AddSingleton<IItemService, ItemService>();

using var provider = svcs.BuildServiceProvider();

var service = provider.GetRequiredService<IItemService>();

service?.Create("item-A", 123);
service?.Create("item-B", 45);

static class CustomLogger
{
    public static void LogInformation(string msg, params object?[]? args)
    {
        var f = new StackFrame(1, true);
        var m = f.GetMethod();

        using (LogContext.PushProperty("TypeName", m?.DeclaringType?.Name))
        using (LogContext.PushProperty("FullTypeName", m?.DeclaringType?.FullName))
        using (LogContext.PushProperty("MethodName", m?.Name))
        using (LogContext.PushProperty("FileName", f.GetFileName()))
        using (LogContext.PushProperty("LineNumber", f.GetFileLineNumber()))
        {
            Log.Information(msg, args);
        }
    }
}

namespace Example.CallerTest
{
    public interface IItemService
    {
        Item Create(string name, int value);
    }

    public record Item(string Id, string Name, int Value);

    class BaseService
    {
        public void Before()
        {
            CustomLogger.LogInformation("before");
        }
    }

    class ItemService : BaseService, IItemService
    {
        public Item Create(string name, int value)
        {
            base.Before();

            var id = Guid.NewGuid().ToString();
            var item = new Item(id, name, value);

            CustomLogger.LogInformation("created item: {result}", item);

            return item;
        }
    }
}