namespace Example.Core
{
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
                logger.LogError(ex, "error handled");

                if (ex is IAppError a)
                {
                    await HandleAppErrorAsync(ctx, a);
                }
                else if (ex is IServiceError s)
                {
                    await HandleServiceErrorAsync(ctx, s);
                }
                else if (ex is IBaseError b)
                {
                    await HandleBaseErrorAsync(ctx, b);
                }
                else
                {
                    await HandleExceptionAsync(ctx, ex);
                }
            }
        }

        private async Task HandleAppErrorAsync(HttpContext ctx, IAppError err)
        {
            logger.LogInformation($"handled AppError: statuscode={err.StatusCode}, message={err.Message}, errorcode={err.ErrorCode}");

            ctx.Response.StatusCode = err.StatusCode;

            await ctx.Response.WriteAsJsonAsync(err);
        }

        private async Task HandleBaseErrorAsync(HttpContext ctx, IBaseError err)
        {
            logger.LogInformation($"handled BaseError: statuscode={err.StatusCode}, message={err.Message}");

            ctx.Response.StatusCode = err.StatusCode;

            await ctx.Response.WriteAsJsonAsync(err);
        }

        private async Task HandleServiceErrorAsync(HttpContext ctx, IServiceError err)
        {
            logger.LogInformation($"handled ServiceError: statuscode={err.StatusCode}, message={err.Message}, content={await err.Content.ReadAsStringAsync()}");

            ctx.Response.StatusCode = err.StatusCode;

            await err.Content.CopyToAsync(ctx.Response.Body);
        }

        private async Task HandleExceptionAsync(HttpContext ctx, Exception ex)
        {
            logger.LogInformation($"handled Exception: {ex.GetType()}");

            ctx.Response.StatusCode = 500;

            await ctx.Response.WriteAsJsonAsync<Dictionary<string, object>>(new()
            {
                ["StatusCode"] = ctx.Response.StatusCode,
                ["Message"] = ex.Message,
            });
        }
    }

    public interface IBaseError
    {
        int StatusCode { get; }
        string Message { get; }
    }

    public interface IAppError : IBaseError
    {
        int IBaseError.StatusCode => 500;
        string ErrorCode { get; }
    }

    public interface IAppInputError : IAppError
    {
        int IBaseError.StatusCode => 400;
    }

    public interface IServiceError : IBaseError
    {
        HttpContent Content { get; }
    }
}
