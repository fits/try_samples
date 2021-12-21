import * as Handlebars from 'handlebars'

const tmpl = Handlebars.compile(`
    {{~!}}list: {{!~}}
    {{#each list}}
        {{~#unless @first}}, {{/unless~}}
        {{~this.name~}}
    {{/each}}
`)

// "list: a, b"
console.log(
    tmpl({ list: [{ name: 'a' }, { name: 'b' }] })
)
