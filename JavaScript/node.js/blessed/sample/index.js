
const blessed = require('blessed')

const map = [
    [1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1],
    [1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1],
    [1, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1],
    [1, 0, 0, 0, 0, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 1],
    [1, 0, 0, 0, 0, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 1],
    [1, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1],
    [1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1],
    [1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 0, 1],
    [1, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 1, 1, 0, 1],
    [1, 0, 1, 1, 1, 0, 0, 0, 1, 0, 0, 0, 1, 1, 0, 1],
    [1, 0, 0, 1, 0, 0, 0, 0, 1, 0, 0, 0, 1, 0, 0, 1],
    [1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1],
    [1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1],
]

const mapping = [
    ' ',
    '*'
]

const p = {x: 1, y: 1}

const layout = () =>
    map.map( (row, y) => 
        row.map( (col, x) => 
            p.x == x && p.y == y ? 'P' : mapping[col]
        ).join('')
    ).join('\n')

const screen = blessed.screen()
const box = blessed.box()

screen.append(box)

const render = () => {
    box.setContent(layout())
    screen.render()
}

box.key(['up', 'down', 'left', 'right'], (ch, key) => {
    //console.log(`${ch}, ${key}`)

    let x = p.x
    let y = p.y

    switch (key.name) {
        case 'up':
            y = Math.max(p.y - 1, 0)
            break
        case 'down':
            y = Math.min(p.y + 1, map.length - 1)
            break
        case 'left':
            x = Math.max(p.x - 1, 0)
            break
        case 'right':
            x = Math.min(p.x + 1, map[0].length - 1)
            break
    }

    if (map[y][x] == 0) {
        p.x = x
        p.y = y

        render()
    }
})

screen.key(['q', 'C-c'], (ch, key) => process.exit(0))

render()
