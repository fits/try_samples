import ollama from 'ollama'

const model = process.argv[2]
const data = process.argv[3]

const content = `
次の購買データを併売分析して下さい。
1行が1つの取引、コンマで区切られたアルファベットが購入商品となっています。

# 購買データ

${data}
`

const msg = {
    role: 'user',
    content
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
