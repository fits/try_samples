using Microsoft.EntityFrameworkCore;

public class CartContext : DbContext
{
    public DbSet<Cart> Carts => Set<Cart>();

    public CartContext(DbContextOptions<CartContext> opts) : base(opts) {}
}
