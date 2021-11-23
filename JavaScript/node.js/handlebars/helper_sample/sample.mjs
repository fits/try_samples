import Handlebars from 'handlebars'

Handlebars.registerHelper('inValues', (value) => {
    return value.map(v => "'" + v.replaceAll(/['\\]/g, '') + "'").join(',')
})

const template = Handlebars.compile(`
  item in ( {{{inValues items}}} )
`)

console.log( template({ items: ['A', "B'C", 'D"E', 'F\G'] }) )
