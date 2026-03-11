using System.Runtime.CompilerServices;
using Serilog;

using Example.CallerTest;

Log.Logger = new LoggerConfiguration()
    .WriteTo.Console(new Serilog.Formatting.Compact.CompactJsonFormatter())
    .CreateLogger();

var svc = new ItemService();

svc.Create("item-A", 123);
svc.Create("item-B", 45);

CustomLogger.LogInformation("from main process");

var f = () => CustomLogger.LogInformation("from lambda func");
f();

static class CustomLogger
{
    public static void LogInformation(string msg,
        object?[]? values = null,
        [CallerMemberName] string memberName = "",
        [CallerFilePath] string filePath = "",
        [CallerLineNumber] int lineNumber = 0)
    {
        if (Log.IsEnabled(Serilog.Events.LogEventLevel.Information))
        {
            Log
                .ForContext("MemberName", memberName)
                .ForContext("FileName", Path.GetFileName(filePath))
                .ForContext("LineNumber", lineNumber)
                .Information(msg, values);
        }
    }
}

namespace Example.CallerTest
{
    public record Item(string Id, string Name, int Value);

    class BaseService
    {
        public void Before()
        {
            CustomLogger.LogInformation("before");
        }
    }

    class ItemService : BaseService
    {
        public Item Create(string name, int value)
        {
            base.Before();

            var id = Guid.NewGuid().ToString();
            var item = new Item(id, name, value);

            CustomLogger.LogInformation("created item: {item}", [item]);

            return item;
        }
    }
}