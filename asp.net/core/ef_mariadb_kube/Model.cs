
public class Cart
{
    public required string CartId { get; set; }
    public required ICollection<CartLine> Lines { get; set; }
}

public class CartLine
{
    public required int CartLineId { get; set; }
    public required Item Item { get; set; }
    public required int Qty { get; set; }
}

public class Item
{
    public required string ItemId { get; set; }
    public required string Name { get; set; }
    public required int Price { get; set; }
}
