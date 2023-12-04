
public class Warmup : IStartupFilter
{
    public Action<IApplicationBuilder> Configure(Action<IApplicationBuilder> next)
    {
        return builder =>
        {
            var ctx = builder.ApplicationServices.GetService(typeof(ItemContext)) as ItemContext;

            Console.WriteLine("dbcontext: {0}", ctx != null);

            _ = ctx?.Model;

            next(builder);
        };
    }
}