using Microsoft.EntityFrameworkCore;

var builder = WebApplication.CreateBuilder(args);

builder.Services.AddDbContextPool<ItemContext>(opt => {
    var dbPath = Environment.GetEnvironmentVariable("DB_PATH") ?? "./sample.db";
    
    opt.UseSqlite($"Data Source={dbPath}");
    opt.UseQueryTrackingBehavior(QueryTrackingBehavior.NoTracking);
});

builder.Services.AddTransient<IStartupFilter, Warmup>();

var app = builder.Build();

app.MapPost("/find", (ItemContext db, FindItem input) => {
    var s = DateTime.Now;

    var res = db.Items.Where(r => r.Name.StartsWith(input.Name)).OrderBy(r => r.Name).Take(10);

    Console.WriteLine("query time: {0}ms", (DateTime.Now - s).TotalMilliseconds);

    return res;
});

app.Run();
