using Microsoft.EntityFrameworkCore;

var builder = WebApplication.CreateBuilder(args);

builder.AddServiceDefaults();

builder.AddMySqlDbContext<ItemContext>("itemdb");

var app = builder.Build();

using (var scope = app.Services.CreateScope())
{
    var ctx = scope.ServiceProvider.GetRequiredService<ItemContext>();
    ctx.Database.EnsureCreated();
}

app.MapPut("/items", async (ItemContext ctx, CreateItem input) =>
{
    var item = new Item
    {
        Id = input.Id,
        Price = input.Price
    };

    ctx.Items.Add(item);
    await ctx.SaveChangesAsync();

    return item;
});

app.MapGet("/items", (ItemContext ctx) => ctx.Items);

app.Run();

record CreateItem(string Id, int Price);

class Item
{
    public required string Id { get; set; }
    public int Price { get; set; }
}

class ItemContext(DbContextOptions<ItemContext> opts) : DbContext(opts)
{
    public DbSet<Item> Items => Set<Item>();
}