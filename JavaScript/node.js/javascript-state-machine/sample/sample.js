
const StateMachine = require('javascript-state-machine')

const fsm = new StateMachine({
    init: 'idle',
    transitions: [
        { name: 'on', from: 'idle', to: 'active' },
        { name: 'off', from: 'active', to: 'idle'}
    ],
    methods: {
        onOn: function() {
            console.log('*** do on')
        },
        onBeforeOn: function() {
            console.log('*** before on')
        },
        onAfterOn: function() {
            console.log('*** after on')
        },
        onOff: function() {
            console.log('*** do off')
        },
        onLeaveIdle: function() {
            console.log('*** leave idle')
        },
        onEnterActive: function() {
            console.log('*** enter active')
        }
    }
})

console.log(fsm.state)

fsm.on()

console.log(fsm.state)

try {
    fsm.on()
} catch(e) {
    console.error(e)
}

console.log(fsm.state)

fsm.off()

console.log(fsm.state)
