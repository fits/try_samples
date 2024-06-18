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

app.MapPost("/create", async (ItemContext ctx, CreateItem input) =>
{
    if (ctx.Items.Any(x => x.Id == input.Id))
    {
        return Results.BadRequest("exists item");
    }

    var item = new Item
    {
        Id = input.Id,
        Price = input.Price
    };

    ctx.Items.Add(item);
    await ctx.SaveChangesAsync();

    return Results.Ok(item);
});

app.MapPost("/find", (ItemContext ctx, FindItem input) => ctx.Items.Find(input.Id));

app.Run();

record CreateItem(string Id, int Price);
record FindItem(string Id);

class Item
{
    public required string Id { get; set; }
    public int Price { get; set; }
}

class ItemContext(DbContextOptions<ItemContext> opts) : DbContext(opts)
{
    public DbSet<Item> Items => Set<Item>();
}