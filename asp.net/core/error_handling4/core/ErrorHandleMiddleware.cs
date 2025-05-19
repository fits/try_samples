
public class ErrorHandleMiddleware(RequestDelegate next, ILogger<ErrorHandleMiddleware> logger)
{
    public async Task InvokeAsync(HttpContext ctx)
    {
        try
        {
            await next(ctx);
        }
        catch (Exception ex)
        {
            logger.LogError(ex, "middleware error handled");

            var res = ex switch
            {
                IInputError i => await HandleInputErrorAsync(ctx, i),
                IAppError a => await HandleAppErrorAsync(ctx, a),
                _ => false,
            };

            if (!res)
            {
                throw;
            }
        }
    }

    private async ValueTask<bool> HandleInputErrorAsync(HttpContext ctx, IInputError ex)
    {
        logger.LogInformation($"handled InputError: {ex.Code}, {ex.Message}");

        ctx.Response.StatusCode = StatusCodes.Status400BadRequest;

        await ctx.Response.WriteAsJsonAsync(ex);

        return true;
    }

    private async ValueTask<bool> HandleAppErrorAsync(HttpContext ctx, IAppError ex)
    {
        logger.LogInformation($"handled AppError: {ex.Code} {ex.Message}");

        ctx.Response.StatusCode = StatusCodes.Status500InternalServerError;

        await ctx.Response.WriteAsJsonAsync(ex);

        return true;
    }
}

public interface IAppError
{
    string Code { get; }
    string Message { get; }
}

public interface IInputError : IAppError
{
    string Property { get; }
}
