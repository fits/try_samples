export const types = {
    create(id) {
        return [ id ]
    },
    changeQty(state, item, qty) {
        if (!state || !item || qty < 0) {
            return null
        }

        if (state.length == 1) {
            if (qty > 0) {
                return [ state[0], [{ item, qty }] ]
            }
        }
        else if (state.length == 2) {
            const [ id, items ] = state

            const [newItems, exists] = items.reduce(
                (acc, x) => {
                    if (x.item === item) {
                        return (qty > 0) ? 
                            [ [ ...acc[0], { item, qty } ], true ] : 
                            [ acc[0], true ]
                    }
                    return [ [ ...acc[0], x ], acc[1] ]
                }, 
                [[], false]
            )

            if (!exists && qty > 0) {
                newItems.push({ item, qty })
            }

            if (newItems.length > 0) {
                return [ id, newItems ]
            }
            else {
                return [ id ]
            }
        }

        return null
    },
}

export const run = {
    run() {
        const s1 = types.create('test-cart')
        console.log(s1)

        const s2 = types.changeQty(s1, 'item-A', 2)
        console.log(s2)
        
        const s3 = types.changeQty(s2, 'item-B', 1)
        console.log(s3)
        
        const s4 = types.changeQty(s3, 'item-A', 3)
        console.log(s4)
        
        const s5 = types.changeQty(s4, 'item-A', 0)
        console.log(s5)
        
        const s6 = types.changeQty(s5, 'item-C', 4)
        console.log(s6)

        const s7 = types.changeQty(s6, 'item-C', 0)
        console.log(s7)

        const s8 = types.changeQty(s7, 'item-B', 0)
        console.log(s8)
    }
}