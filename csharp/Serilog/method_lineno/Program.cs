using System.Runtime.CompilerServices;
using Microsoft.Extensions.DependencyInjection;
using Serilog;
using Serilog.Context;

using Example.CallerTest;

Log.Logger = new LoggerConfiguration()
    .Enrich.FromLogContext()
    .WriteTo.Console(
        outputTemplate: "{Timestamp:yyyy-MM-dd HH:mm:ss.fff} [{Level:u3}] ({FullTypeName}) ({TypeName}.{MemberName} {FilePath}:{LineNumber}) {Message:lj}{NewLine}{Exception}"
    )
    .CreateLogger();

var svcs = new ServiceCollection();

svcs.AddSingleton<IItemService, ItemService>();

using var provider = svcs.BuildServiceProvider();

var service = provider.GetRequiredService<IItemService>();

service?.Create("item-A", 123);
service?.Create("item-B", 45);

static class CustomLogger<T>
{
    public static void LogInformation(string msg,
        object?[]? args = null,
        [CallerMemberName] string memberName = "",
        [CallerFilePath] string filePath = "",
        [CallerLineNumber] int lineNumber = 0)
    {
        using (LogContext.PushProperty("TypeName", typeof(T).Name))
        using (LogContext.PushProperty("FullTypeName", typeof(T).FullName))
        using (LogContext.PushProperty("MemberName", memberName))
        using (LogContext.PushProperty("FilePath", filePath))
        using (LogContext.PushProperty("LineNumber", lineNumber))
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
            CustomLogger<BaseService>.LogInformation("before");
        }
    }

    class ItemService : BaseService, IItemService
    {
        public Item Create(string name, int value)
        {
            base.Before();

            var id = Guid.NewGuid().ToString();
            var item = new Item(id, name, value);

            CustomLogger<ItemService>.LogInformation("created item: {item}", [item]);

            return item;
        }
    }
}