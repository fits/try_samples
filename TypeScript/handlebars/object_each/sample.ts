import * as Handlebars from 'handlebars'

const tmpl = Handlebars.compile(`
{{#if sort}}
ORDER BY {{!~}}
{{#each sort}}
    {{~#each this~}}
        {{#unless @../first}}, {{else}}{{#unless @first}}, {{/unless}}{{/unless}}
        {{~@key}} {{this~}}
    {{/each}}
{{/each}}
{{/if}}
`)

console.log(tmpl({}))

console.log('-----')

// "ORDER BY date DESC, price ASC, name ASC"
console.log(
    tmpl({
        sort: [
            { date: 'DESC', price: 'ASC' }, 
            { name: 'ASC' }
        ]
    })
)
