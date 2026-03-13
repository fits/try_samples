using Serilog;
using Serilog.Sinks.InMemory;

using BenchmarkDotNet.Attributes;
using BenchmarkDotNet.Running;

BenchmarkRunner.Run(typeof(Program).Assembly);

[ShortRunJob]
public class ServiceBenchmark
{
    private readonly Service svc1;
    private readonly Service svc2;

    public ServiceBenchmark()
    {
        svc1 = new Service(
            new LoggerConfiguration()
                .WriteTo.InMemory()
                .CreateLogger()
        );

        svc2 = new Service(
            new LoggerConfiguration()
                .Enrich.WithProcessId()
                .Enrich.WithThreadId()
                .Enrich.WithThreadName()
                .Enrich.WithMachineName()
                .Enrich.WithEnvironmentUserName()
                .WriteTo.InMemory()
                .CreateLogger()
        );
    }

    [Benchmark]
    public string Case1() => svc1.Exec("case-1");

    [Benchmark]
    public string Case2() => svc2.Exec("case-2");
}