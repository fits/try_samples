
/* @jsx el */
const el = (name, props, children) => {
    return `name: ${name}, props: ${JSON.stringify(props)}, children: ${children}`
}

const d = <div id="a1">abc123</div>

console.log(d)
