
export const state = () => ({
    items: []
})

export const mutations = {
    update (state, items) {
        state.items = items
    }
}
