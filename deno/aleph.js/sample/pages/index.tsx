import { Link } from 'https://deno.land/x/aleph/mod.ts'
import React, { useState } from 'https://esm.sh/react'

export default function Home() {
    const [count, setCount] = useState(0)

    return (
        <div>
            <div><Link to="/page1">page1</Link></div>
            <h1>Counter</h1>
            <div>
                <button onClick={() => setCount(n => n - 1)}> - </button>
                <button onClick={() => setCount(n => n + 1)}> + </button>
                count:<span>{count}</span>
            </div>
        </div>
    )
}
