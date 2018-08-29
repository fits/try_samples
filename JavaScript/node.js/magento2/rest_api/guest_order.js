
const fetch = require('node-fetch')
const config = require('./conf/config.json')

const itemSku = process.argv[2]
const userAddress = require(process.argv[3])

const url = path => `${config.baseUrl}/${path}`

const headers = token => {
    const res = {
        'Content-Type': 'application/json'
    }

    if (token) {
        res['Authorization'] = `Bearer ${token}`
    }

    return res
}

const dump = r => {
    console.log(r)
    return r
}

const request = (method, path, token, body) => {
    const params = {
        method: method,
        headers: headers(token)
    }

    if (body) {
        params['body'] = JSON.stringify(body)
    }

    return fetch(url(path), params)
        .then(r =>
            r.json().then(json => [json, r.ok])
        )
        .then(([json, res]) => {
            if (!res) {
                throw new Error(JSON.stringify(json))
            }

            return json
        })
        .then(dump)
}

const post = (path, token, body) => request('POST', path, token, body)

const token = () =>
    post('integration/admin/token', null, {
        username: config.user, password: config.password
    }).then(token => new Object({token: token}))

const cart = info =>
    post('guest-carts', info.token)
        .then(id => Object.assign({cartId: id}, info))

const addItem = sku => info =>
    post(`guest-carts/${info.cartId}/items`, info.token, {
        cartItem: { sku: sku, qty: 1, quoteId: info.cartId }
    }).then(r => info)

const shippingInformation = address => info =>
    post(`guest-carts/${info.cartId}/shipping-information`, info.token, {
        addressInformation: {
            shippingAddress: address,
            shippingMethodCode: config.shippingMethodCode,
            shippingCarrierCode: config.shippingCarrierCode,
            billingAddress: address
        }
    }).then(r => info)

// Order
const paymentInformation = email => info =>
    post(`guest-carts/${info.cartId}/payment-information`, info.token, {
        email: email,
        paymentMethod: {
            method: config.paymentMethod
        }
    }).then(id => Object.assign({orderId: id}, info))


token()
    .then(cart)
    .then(addItem(itemSku))
    .then(shippingInformation(userAddress))
    .then(paymentInformation(userAddress.email))
    .then(r => console.log(r))
    .catch(err => console.error(err))
