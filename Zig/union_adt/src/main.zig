const std = @import("std");

const OrderLine = struct {
    item: []const u8,
    qty: usize,
};

const EmptyOrder = struct {
    id: []const u8,
};

const ActiveOrder = struct {
    id: []const u8,
    lines: std.ArrayList(OrderLine),

    fn init(id: []const u8) ActiveOrder {
        return .{
            .id = id,
            .lines = .empty,
        };
    }

    fn deinit(self: *ActiveOrder, al: std.mem.Allocator) void {
        self.lines.deinit(al);
    }
};

const OrderError = error{
    InvalidState,
};

const Order = union(enum) {
    ready: EmptyOrder,
    active: ActiveOrder,

    pub fn init(id: []const u8) Order {
        return .{
            .ready = .{
                .id = id,
            },
        };
    }

    pub fn start(self: *const Order) OrderError!Order {
        return switch (self.*) {
            Order.ready => |x| .{
                .active = ActiveOrder.init(x.id),
            },
            Order.active => OrderError.InvalidState,
        };
    }

    pub fn deinit(self: *Order, al: std.mem.Allocator) void {
        switch (self.*) {
            Order.ready => {},
            Order.active => |*x| x.deinit(al),
        }
    }
};

fn printOrder(s: Order) void {
    switch (s) {
        Order.ready => |x| std.log.info("empty: id={s}", .{x.id}),
        Order.active => |x| {
            std.log.info("active: id={s}", .{x.id});

            for (x.lines.items) |it| {
                std.log.info("item: {s}, qty: {d}", .{it.item, it.qty});
            }

            std.log.info("-----", .{});
        },
    }
}

pub fn main() !void {
    const s = Order.init("order-1");
    printOrder(s);

    const al = std.heap.smp_allocator;

    var s2 = try s.start();
    defer s2.deinit(al);

    printOrder(s2);

    try s2.active.lines.append(
        al,
        .{
            .item = "A",
            .qty = 2,
        },
    );

    try s2.active.lines.append(
        al,
        .{
            .item = "B",
            .qty = 3,
        },
    );

    printOrder(s2);
}