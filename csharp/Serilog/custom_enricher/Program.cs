using System.Diagnostics;
using Microsoft.Extensions.DependencyInjection;
using Microsoft.Extensions.Logging;
using Serilog;
using Serilog.Core;
using Serilog.Events;

using Example.EnricherTest;

Log.Logger = new LoggerConfiguration()
    .Enrich.With<StackTraceEnricher>()
    .WriteTo.Console(
        outputTemplate: "{Timestamp:yyyy-MM-dd HH:mm:ss.fff} [{Level:u3}] ({SourceContext}) ({FullTypeName}) ({TypeName}.{MethodName} {FileName}:{LineNumber}) {Message:lj}{NewLine}{Exception}"
    )
    .CreateLogger();

var svcs = new ServiceCollection();

svcs.AddLogging(b => b.AddSerilog(dispose: true));
svcs.AddSingleton<IItemService, ItemService>();

using var provider = svcs.BuildServiceProvider();

var service = provider.GetRequiredService<IItemService>();

service?.Create("item-A", 123);
service?.Create("item-B", 45);


public sealed class StackTraceEnricher : ILogEventEnricher
{
    public void Enrich(LogEvent logEvent, ILogEventPropertyFactory propertyFactory)
    {
        var srcType = Type.GetType(logEvent.Properties["SourceContext"].ToString("l", null));

        if (srcType == null)
        {
            Console.WriteLine("srctype null");
            return;
        }

        var st = new StackTrace(true);

        for (int i = 1; i < st.FrameCount; i++)
        {
            var f = st.GetFrame(i);

            if (f != null && IsTargetFrame(srcType, f))
            {
                logEvent.AddPropertyIfAbsent(propertyFactory.CreateProperty(
                    "LineNumber",
                    f.GetFileLineNumber()
                ));

                logEvent.AddPropertyIfAbsent(propertyFactory.CreateProperty(
                    "FileName",
                    f.GetFileName()
                ));

                var m = f.GetMethod()!;

                logEvent.AddPropertyIfAbsent(propertyFactory.CreateProperty(
                    "MethodName",
                    m.Name
                ));

                logEvent.AddPropertyIfAbsent(propertyFactory.CreateProperty(
                    "TypeName",
                    m.DeclaringType!.Name
                ));

                logEvent.AddPropertyIfAbsent(propertyFactory.CreateProperty(
                    "FullTypeName",
                    m.DeclaringType!.FullName
                ));

                return;
            }
        }
    }

    private bool IsTargetFrame(Type srcType, StackFrame f)
    {
        var m = f.GetMethod();

        if (m == null)
        {
            return false;
        }

        var t = m.DeclaringType;

        if (t == null)
        {
            return false;
        }

        var typeName = t.FullName ?? t.Name;

        if (typeName.StartsWith("Serilog.") || typeName.StartsWith("Microsoft.Extensions."))
        {
            return false;
        }

        return t.IsAssignableFrom(srcType);
    }
}

namespace Example.EnricherTest
{
    public interface IItemService
    {
        Item Create(string name, int value);
    }

    public record Item(string Id, string Name, int Value);

    class BaseService(ILogger<BaseService> log)
    {
        public void Before()
        {
            log.LogInformation("before");
        }
    }

    class ItemService(ILogger<ItemService> log) : BaseService(log), IItemService
    {
        public Item Create(string name, int value)
        {
            base.Before();

            var id = Guid.NewGuid().ToString();
            var item = new Item(id, name, value);

            log.LogInformation("created item: {item}", item);

            return item;
        }
    }
}