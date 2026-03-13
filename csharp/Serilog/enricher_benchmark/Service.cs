using Serilog;

public class Service : BaseService
{
    public Service(ILogger logger) : base(logger)
    {
    }

    protected override string ConcreteExec(string msg)
    {
        _logger.Information("start: {param}", msg);

        var res = $"{msg}_sub";

        _logger.Information("end: {result}", res);

        return res;
    }
}