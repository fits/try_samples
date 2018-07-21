
import fetch from 'node-fetch'
import { Future } from 'funfix'
import { JSDOM, HTMLAnchorElement } from 'jsdom'

const url = 'https://www.google.com/search?q='

const keyword = process.argv[2]

const scrape_link = doc => 
    Array.from(doc.querySelectorAll('h3.r a'))
            .map( (a: HTMLAnchorElement) => [a.textContent, a.href] )

Future.fromPromise(fetch(`${url}${keyword}`))
    .flatMap(r => Future.fromPromise(r.text()))
    .map(s => new JSDOM(s).window.document)
    .map(scrape_link)
    .onComplete(r =>
        r.fold(
            err => console.error(err),
            res => console.log(res)
        )
    )
