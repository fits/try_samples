using Microsoft.EntityFrameworkCore;

var builder = WebApplication.CreateBuilder(args);

builder.Services.AddDbContextPool<RecContext>(opt => {
    var conStr = Environment.GetEnvironmentVariable("DB_CONNECTION") ?? "server=db.example.com;user=root;password=pass;database=sample;DnsCheckInterval=3";
    var version = ServerVersion.AutoDetect(conStr);

    opt.UseMySql(conStr, version);
});

var app = builder.Build();

app.Services.UseMySqlConnectorLogging();

app.MapPost("/entry", async (RecContext db) => {
    var rec = new Rec
    {
        CreatedAt = DateTime.Now
    };

    db.Recs.Add(rec);
    await db.SaveChangesAsync();

    return rec;
});

app.Run();
