using Microsoft.Extensions.Logging;

using ILoggerFactory factory = LoggerFactory.Create(b =>
{
    b.AddConsole();
});

var logger = factory.CreateLogger("logging test");

var d1 = new Data("test1", 123);

logger.LogInformation("a. d1={d}", d1);
logger.LogInformation($"b. d1={d1}");
Console.WriteLine("c. d1={0}", d1);

var d2 = new Data2("test2", 456);

logger.LogInformation("a. d2={d}", d2);
logger.LogInformation($"b. d2={d2}");
Console.WriteLine("c. d2={0}", d2);

record Data(string id, int value);

public class Data2(string id, int value)
{
    public String Id => id;
    public int Value => value;
}
