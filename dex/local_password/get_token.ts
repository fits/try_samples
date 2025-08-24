import { encodeBase64 } from "jsr:@std/encoding/base64"

const clientId = Deno.args[0]
const secret = Deno.args[1]
const user = Deno.args[2] // email
const password = Deno.args[3]

const url = 'http://127.0.0.1:8080/dex/token'

const headers = {
    Authorization: 'Basic ' + encodeBase64(`${clientId}:${secret}`),
    'Content-Type': 'application/x-www-form-urlencoded',
}

const body = [
    'grant_type=password',
    // 'scope=openid', // no refresh_token
    'scope=openid offline_access',
    `username=${user}`,
    `password=${password}`,
].join('&')

const res = await fetch(url, { method: 'POST', headers, body })

const token = await res.text()

console.log(token)
