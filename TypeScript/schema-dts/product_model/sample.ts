import { Brand, Offer, Product, ProductModel, WithContext } from 'schema-dts'

const p: WithContext<Product> = {
    '@context': 'https://schema.org',
    '@type': 'Product',
    identifier: 'item001',
    name: 'item-1',
    brand: <Brand>{
        '@type': 'Brand',
        name: 'A-1'
    },
    model:[
        <ProductModel>{
            '@type': 'ProductModel',
            identifier: 'item001-01',
            name: 'item-1 white',
            color: 'white',
            offers: {
                '@type': 'Offer',
                price: 1000,
                availability: 'https://schema.org/InStock'
            }
        },
        <ProductModel>{
            '@type': 'ProductModel',
            identifier: 'item001-02',
            name: 'item-1 black',
            color: 'black',
            offers: [
                <Offer>{
                    '@type': 'Offer',
                    price: 700,
                    validFrom: '2021-12-01T00:00:00Z',
                    validThrough: '2021-12-10T00:00:00Z',
                    availability: 'https://schema.org/SoldOut'
                },
                <Offer>{
                    '@type': 'Offer',
                    price: 900,
                    validFrom: '2021-12-10T00:00:00Z',
                    availability: 'https://schema.org/InStock'
                }
            ]
        }
    ]
}

console.log(JSON.stringify(p, null, 2))
