export function findPrice(itemId) {
    console.log(`* called findPrice(${itemId})`)

    if (itemId.trim().length < 3) {
        return null
    }

    return 123
}

export const run = {
    run() {
        const id1 = '   '
        const id2 = 'a1'
        const id3 = 'item-1'

        const p1 = findPrice(id1)
        const p2 = findPrice(id2)
        const p3 = findPrice(id3)

        console.log(`id=${id1}, price=${p1}`)
        console.log(`id=${id2}, price=${p2}`)
        console.log(`id=${id3}, price=${p3}`)
    }
}
