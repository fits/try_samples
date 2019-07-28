
local data = [ { name: 'user' + i, point: i * 10 } for i in std.range(0, 5) ];

local last(us) = us[std.length(us) - 1];

{
    users: [ { name: u.name } for u in data ],
    first: self.users[0],
    last: self.users[std.length(self.users) - 1],
    nest1: {
        first: $['users'][0],
        last: last($['users']),
        users: [ u.name for u in $['users'] ]
    },
    nest2: {
        local u = $['users'],
        first: u[0],
        last: last(u),
        users: { [u.name]: u.point for u in data }
    }
}
