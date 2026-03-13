using Serilog;

public abstract class BaseService
{
    protected readonly ILogger _logger;

    public BaseService(ILogger logger)
    {
        _logger = logger;
    }

    public string Exec(string msg)
    {
        _logger.Information("Exec start");

        var res = $"{ConcreteExec(msg)}_base";

        _logger.Information("Exec end");

        return res;
    }

    protected abstract string ConcreteExec(string msg);
}