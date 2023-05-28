using Microsoft.EntityFrameworkCore;
using System.ComponentModel.DataAnnotations.Schema;

var dsn = "server=mysql1;database=sample;user=root";

var builder = WebApplication.CreateBuilder(args);

// builder.Services.AddHostedService<WarmUpService>();

builder.Services.AddDbContext<ItemContext>(opt => {    
    opt.UseMySQL(dsn);
});

var app = builder.Build();

app.MapPost("/find", (ItemContext db, FindItem input) => 
    db.Items.Where(r => r.Price >= input.Price).OrderBy(r => r.Price)
);

app.Run();


record FindItem(int Price);

[Table("items")]
public class Item
{
    [Column("id")]
    public required string Id { get; set; }
    
    [Column("price")]
    public int Price { get; set; }
}

public class ItemContext : DbContext
{
    public DbSet<Item> Items => Set<Item>();

    public ItemContext(DbContextOptions<ItemContext> opts) : base(opts) {}
}

// public class WarmUpService : IHostedService
// {
//     private readonly ItemContext _db;

//     public WarmUpService(ItemContext db)
//     {
//         _db = db;
//     }

//     public Task StartAsync(CancellationToken cancellationToken)
//     {
//         _db.Items.SingleOrDefault(r => r.Id == "");
        
//         return Task.CompletedTask;
//     }

//     public Task StopAsync(CancellationToken cancellationToken)
//     {
//         return Task.CompletedTask;
//     }
// }