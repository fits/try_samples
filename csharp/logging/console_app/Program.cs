using Microsoft.Extensions.Logging;

using ILoggerFactory factory = LoggerFactory.Create(b =>
{
    b.AddConsole();
});

var logger = factory.CreateLogger("logging test");

logger.LogInformation("{1}, {0}, {1}", 0, 1, 2); // 0, 1, 2

var a = "A";
var b = "BB";
var c = "CCC";

logger.LogInformation("{c}, {b}, {a}", a, b, c); // A, BB, CCC
logger.LogInformation("{1}, {2}, {0}", a, b, c); // A, BB, CCC
logger.LogInformation("{c}, {a}, {c}", a, b, c); // A, BB, CCC

logger.LogInformation($"{c}, {b}, {a}"); // CCC, BB, A

Console.WriteLine("{1}, {2}, {0}", a, b, c); // BB, CCC, A
