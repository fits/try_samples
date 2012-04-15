http = require 'http'
url = require 'url'
fs = require 'fs'
path = require 'path'

dir = process.argv[2]

# エラーメッセージの出力
printError = (urlString, error) ->
	console.log "failed: #{urlString}, #{error.message}"

process.stdin.resume()

process.stdin.on 'data', (urls) ->
	urls.toString().trim().split('\n').forEach (u) ->
		trgUrl = url.parse u

		# URL 接続
		req = http.get trgUrl, (res) ->
			res.setEncoding 'binary'
			buf = ''

			# データダウンロード
			res.on 'data', (chunk) -> buf += chunk

			# ダウンロード完了時の処理
			res.on 'end', ->
				filePath = path.join dir, path.basename(trgUrl.pathname)

				# ファイル出力
				fs.writeFile filePath, buf, 'binary', (err) ->
					if err
						printError trgUrl.href, err
					else
						console.log "downloaded: #{trgUrl.href} => #{filePath}"

			# 接続後のエラー処理
			res.on 'close', (err) -> printError trgUrl.href, err if err

		req.on 'error', (err) -> printError trgUrl.href, err
