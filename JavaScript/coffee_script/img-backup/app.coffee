fs = require 'fs'
restify = require 'restify'
glob = require 'glob'

imgDir = './img'
baseUrl = 'http://localhost:8080/img'
backupSuffix = '.backup'

server = restify.createServer()

server.get /^\/img/, restify.serveStatic
	directory: "#{imgDir}/.."

server.get /\.(html|js|css|png|jpeg)/, restify.serveStatic
	directory: './public'

# ディレクトリの一覧取得
server.get '/list', (req, res, next) ->
	glob "#{imgDir}/*", [], (err, files) ->
		if err
			console.error err
			next err
		else
			res.json files.map (file) -> file.replace "#{imgDir}/", ''
			next()

# 指定ディレクトリ内の画像一覧を取得する
server.get '/image/:dir', (req, res, next) ->
	dir = req.params.dir

	glob "#{imgDir}/#{dir}/*.jpg", [], (err, files) ->
		if err
			console.error err
			next err
		else
			res.json files.map (file) ->
				dir: dir
				name: file.split('/').pop()
				url: file.replace imgDir, baseUrl
				done: fs.existsSync "#{file}#{backupSuffix}"

			next()

# バックアップの作成もしくは削除を実施する
server.get '/backup/:dir/:name', (req, res, next) ->
	dir = req.params.dir
	name = req.params.name

	backupFile = "#{imgDir}/#{dir}/#{name}#{backupSuffix}"

	fsCallback = (err) ->
		if err
			console.error err
			next err
		else
			res.json
				done: fs.existsSync backupFile
			next()

	fs.exists backupFile, (exists) ->
		if exists
			fs.unlink backupFile, fsCallback
		else
			fs.link "#{imgDir}/#{dir}/#{name}", backupFile, fsCallback

# サーバー起動
server.listen 8080, -> console.log 'server started ...'
