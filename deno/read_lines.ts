import { TextLineStream } from 'jsr:@std/streams/text-line-stream'

const stream = Deno.stdin.readable
    .pipeThrough(new TextDecoderStream())
    .pipeThrough(new TextLineStream())

for await (const line of stream) {
    const d = line.trim()

    console.log(d)
}
