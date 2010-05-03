# coding: utf-8
#
#WebDAV（IIS）に接続してファイルを登録するサンプル

from System import *
from System.Net import *

def printException(ex):
    print "%s, %s" % (ex.Status, ex.Message)


#コレクションを作成する
def mkcol(wc, url):

    print "--- mkcol= %s" % url

    try:
        wc.UploadString(url, "MKCOL", "")

    except WebException, ex:
        printException(ex)

#ファイルを配置する
def put_file(wc, url, file):

    print "--- put file= %s" % file

    try:
        wc.UploadFile(url, "PUT", file)

    except WebException, ex:
        printException(ex)

wc = WebClient()
wc.BaseAddress = "http://localhost/"

#Basic認証の利用設定
ccache = CredentialCache()
ccache.Add(Uri(wc.BaseAddress), "Basic", NetworkCredential("tester", "tester"))
ccache.Add(Uri(wc.BaseAddress), "Digest", NetworkCredential("tester", "tester"))
wc.Credentials = ccache

#コレクション作成
mkcol(wc, "webdav/テスト1")
mkcol(wc, "webdav/テスト1")
mkcol(wc, "webdav/テスト1/a")

#ファイル配置
put_file(wc, "webdav/テスト1/a/test.log", "data1.txt")

#コレクション削除
wc.UploadString("webdav/テスト1", "DELETE", "")
