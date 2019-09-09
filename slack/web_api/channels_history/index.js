
const { WebClient } = require('@slack/web-api')

const channelId = process.argv[2]

const token = process.env.SLACK_TOKEN

const client = new WebClient(token)

const history = async () => {
    const res = await client.channels.history({ channel: channelId, count: 5 })
    
    console.log(res)
}

history()
