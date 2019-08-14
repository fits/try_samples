
local items = [ { id: i, name: 'item-' + i } for i in std.range(1, 3) ];
local last(xs) = xs[std.length(xs) - 1];

std.manifestYamlDoc({
    base_items: items,
    products: [ { name: it.name, qty: 1 } for it in items ],
    first: self.products[0],
    last: last(self.products),
    ref: {
        local ps = $['products'],
        products_md5: { [p.name]: std.md5(p.name) for p in ps }
    },
})
