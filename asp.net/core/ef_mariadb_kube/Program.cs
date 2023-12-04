using Microsoft.EntityFrameworkCore;

var builder = WebApplication.CreateBuilder(args);

builder.Services.AddHealthChecks();

builder.Services.AddDbContextPool<CartContext>(opt => {
    var conStr = Environment.GetEnvironmentVariable("DB_CONNECTION") ?? "server=localhost;user=root;password=pass;database=sample";
    var version = ServerVersion.AutoDetect(conStr);

    opt.UseMySql(conStr, version);
});

builder.Services.AddTransient<IStartupFilter, Warmup>();

var app = builder.Build();

app.MapHealthChecks("/healthz");

app.MapPost("/find", async (CartContext db, FindCart input) => {
    var s = DateTime.Now;

    var res = await db.Carts
        .Include(p => p.Lines)
        .ThenInclude(c => c.Item)
        .FirstOrDefaultAsync(r => r.CartId == input.Id);

    Console.WriteLine("query time: {0}ms", (DateTime.Now - s).TotalMilliseconds);

    return res;
});

app.Run();

record FindCart(string Id);
