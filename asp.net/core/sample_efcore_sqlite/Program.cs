using Microsoft.EntityFrameworkCore;

var builder = WebApplication.CreateBuilder(args);

builder.Services.AddDbContextPool<ItemContext>(opt => {
    var dbPath = Environment.GetEnvironmentVariable("DB_PATH") ?? "./sample.db";
    
    opt.UseSqlite($"Data Source={dbPath}");
    opt.UseQueryTrackingBehavior(QueryTrackingBehavior.NoTracking);
});

var app = builder.Build();

var findItems = EF.CompileAsyncQuery((ItemContext ctx, string name) => 
    ctx.Items.Where(r => r.Name.StartsWith(name)).OrderBy(r => r.Name).Take(10)
);

app.MapPost("/create", async (ItemContext db, CreateItem input) => {
    var item = new Item
    {
        Id = Guid.NewGuid().ToString(),
        Name = input.Name,
        Value = input.Value
    };

    db.Items.Add(item);
    await db.SaveChangesAsync();

    return item;
});

app.MapPost("/find", (ItemContext db, FindItem input) => 
    db.Items.Where(r => r.Name.StartsWith(input.Name)).OrderBy(r => r.Name).Take(10)
);

app.MapPost("/find2", (ItemContext db, FindItem input) => 
    findItems(db, input.Name)
);

app.MapPost("/find3", (ItemContext db, FindItem input) => 
    db.Items.FromSqlRaw($"select * from items where name like '{input.Name}%' order by name limit 10")
);

app.Run();
