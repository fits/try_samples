using System.Runtime.CompilerServices;

var svc = new ItemService();
svc.Create("check1");

var traceInfo = (string msg) => TraceLogger.Info(msg);
traceInfo("check2");

public static class TraceLogger
{
    public static void Info(string msg,
        [CallerMemberName] string memberName = "",
        [CallerFilePath] string filePath = "",
        [CallerLineNumber] int lineNumber = 0)
    {
        Console.WriteLine($"message='{msg}', method='{memberName}', file='{Path.GetFileName(filePath)}', lineNo={lineNumber}");
    }
}

public class ItemService
{
    public bool Create(string id)
    {
        TraceLogger.Info($"Create:{id}");
        return true;
    }
}
