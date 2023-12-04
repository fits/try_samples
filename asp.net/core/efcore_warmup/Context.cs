using Microsoft.EntityFrameworkCore;

public class ItemContext : DbContext
{
    public DbSet<Item> Items => Set<Item>();

    public ItemContext(DbContextOptions<ItemContext> opts) : base(opts) {}
}
