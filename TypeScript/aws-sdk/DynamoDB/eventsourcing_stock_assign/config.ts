
export interface DbConfig {
    endpoint?: string
}

export const config: DbConfig = {}

if (process.env['DYNAMODB_ENDPOINT']) {
    config.endpoint = process.env['DYNAMODB_ENDPOINT']
}
