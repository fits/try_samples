module.exports = {
    testEnvironment: 'jsdom',
    preset: 'ts-jest',
    transform: {
        '.*\\.(vue)$': 'vue-jest'
    },
    setupFiles: ['./jest.setup.ts']
}
