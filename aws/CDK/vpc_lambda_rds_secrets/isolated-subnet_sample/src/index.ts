
import { 
    SecretsManagerClient, GetSecretValueCommand
} from '@aws-sdk/client-secrets-manager'

import { createConnection } from 'mysql'

const secretClient = new SecretsManagerClient({})

const connect = (conn) => new Promise((resolve, reject) => {
    conn.connect(err => {
        if (err) {
            reject(err)
        }
        else {
            resolve(null)
        }
    })
})

const end = (conn) => new Promise((resolve, reject) => {
    conn.end(err => {
        if (err) {
            reject(err)
        }
        else {
            resolve(null)
        }
    })
})

type QueryResult = { results, fields }

const query = (conn, sql, values): Promise<QueryResult> => new Promise((resolve, reject) => {
    conn.query(sql, values, (err, results, fields) => {
        if (err) {
            reject(err)
        }
        else {
            resolve({ results, fields })
        }
    })
})

type Credential = { username, password }

let cred: Credential | undefined

export const handler = async (event) => {
    if (!cred) {
        const secret = await secretClient.send(new GetSecretValueCommand({
            SecretId: process.env.RDS_SECRET_NAME
        }))

        cred = JSON.parse(secret.SecretString ?? '')
    }

    const conn = createConnection({
        host: process.env.RDS_ENDPOINT,
        user: cred?.username,
        password: cred?.password
    })

    await connect(conn)

    try {
        const { results } = await query(conn, 'show databases', [])

        console.log(results)

    } finally {
        await end(conn)
    }
}
