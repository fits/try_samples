using Microsoft.EntityFrameworkCore;

public class RecContext : DbContext
{
    public DbSet<Rec> Recs => Set<Rec>();

    public RecContext(DbContextOptions<RecContext> opts) : base(opts) {}
}
