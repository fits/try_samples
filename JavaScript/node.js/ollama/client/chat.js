import ollama from 'ollama'

const model = process.argv[2]
const content = process.argv[3]

const msg = {
    role: 'user',
    content,
}

const res = await ollama.chat({
    model,
    messages: [msg],
})

console.log(res.message.content)

if (res.message.thinking) {
    console.log('# Thinking')
    console.log(res.message.thinking)
}
