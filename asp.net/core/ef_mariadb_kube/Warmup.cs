
public class Warmup : IStartupFilter
{
    public Action<IApplicationBuilder> Configure(Action<IApplicationBuilder> next)
    {
        return builder =>
        {
            var ctx = builder.ApplicationServices.GetService(typeof(CartContext)) as CartContext;

            _ = ctx?.Model;

            next(builder);
        };
    }
}
