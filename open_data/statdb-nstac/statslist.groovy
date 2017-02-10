@Grab('com.mashape.unirest:unirest-java:1.4.7')
import com.mashape.unirest.http.Unirest

/**
 * 次世代統計利用システム API を使って統計表情報を取得するスクリプト
 */

def enc = 'UTF-8'

def appId = args[0]
def statsCode = args[1]

def url = "http://api.e-stat.go.jp/rest/2.0/app/getStatsList?appId=${appId}&statsCode=${statsCode}"

def res = Unirest.get(url).asXml()

println res.body
