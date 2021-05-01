/** @jsx h */
import { render, h } from 'preact'
import { useState, StateUpdater } from 'preact/hooks'

const ValueInput = (p: { label: string, state: [string, StateUpdater<string>] }) => {
    const [ value, setValue ] = p.state

    const change = ev => setValue(ev.target.value)

    return (
        <div>
            <p>
                { p.label } : 
                <input type="text" value={ value } onInput={ change } />
            </p>
        </div>
    )
}

const Result = (p: { first: string, second: string }) => {
    return (
        <div>
            <p>
                { [p.first, p.second].filter(i => i.trim() != '').join(', ') }
            </p>
        </div>
    )
}

const App = () => {
    const firstState = useState('')
    const secondState = useState('')

    return (
        <div>
            <ValueInput label="first" state={ firstState } />
            <ValueInput label="second" state={ secondState } />
            <hr />
            <Result first={ firstState[0] } second={ secondState[0] } />
        </div>
    )
}

render(<App />, document.getElementById('app'))
