
import { RequestOptions } from 'http'
import { CredentialProvider } from '@aws-sdk/types'
import { defaultProvider } from '@aws-sdk/credential-provider-node'
import { sign } from 'aws4'
import { Client, Connection } from '@opensearch-project/opensearch'
import { ConnectionOptions } from '@opensearch-project/opensearch/lib/Connection'

export async function createClient(node: string, 
    provider: CredentialProvider = defaultProvider()) {

    const credential = await provider()

    class AwsConnection extends Connection {
        public constructor(opts?: ConnectionOptions) {
            super(opts)

            const request = this.makeRequest

            this.makeRequest = (options: RequestOptions) => 
                request(sign({ ...options, service: 'es' }, credential))
        }
    }

    return new Client({ 
        node,
        Connection: AwsConnection
    })
}
