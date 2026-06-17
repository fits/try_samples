const std = @import("std");

const OrderLine = struct {
    item: []const u8,
    qty: usize,
};

const Order = struct {
    id: []const u8,
    lines: []const OrderLine,

    pub fn init(al: std.mem.Allocator, id: []const u8, item: []const u8, qty: usize) !Order {
        const lines = try al.alloc(OrderLine, 1);

        lines[0] = .{
            .item = item,
            .qty = qty,
        };

        return .{
            .id = id,
            .lines = lines,
        };
    }

    pub fn deinit(self: *const Order, al: std.mem.Allocator) void {
        al.free(self.lines);
    }
};

pub fn main() void {
    const o1 = Order{
        .id = "order1",
        .lines = &.{
            .{
                .item = "A",
                .qty = 2,
            },
        },
    };

    std.log.info("order1-1: {}", .{o1});
    std.log.info("order1-2: id={s}, item={s}, qty={d}", .{o1.id, o1.lines[0].item, o1.lines[0].qty});

    const al = std.heap.smp_allocator;

    const o2 = Order.init(al, "order2", "B", 3);

    if (o2) |x| {
        defer x.deinit(al);

        std.log.info("order2-1: {}", .{x});
        std.log.info("order2-2: id={s}, item={s}, qty={d}", .{x.id, x.lines[0].item, x.lines[0].qty});
    } else |e| {
        std.log.err("ERROR: {}", .{e});
    }
}