# coding: utf-8
#
#WebDAV（IIS）に接続してファイルを登録するサンプル

from System import *
from System.IO import *
from System.Net import *

def printException(ex):
    print "%s, %s" % (ex.Status, ex.Message)

#ファイルを配置する
def put_file(wc, file):

    print "--- put file= %s" % file

    try:
        fi = FileInfo(file)
        wc.ContentLength = fi.Length

        fs = fi.OpenRead()
        st = wc.GetRequestStream()

        buf = Array.CreateInstance(Byte, 1024 * 100);
        len = 0

        try:
            while True:
                len = fs.Read(buf, 0, buf.Length)

                if len > 0:
                    st.Write(buf, 0, len)
                    st.Flush()

                else:
                    break
        finally:
            fs.Close()
            fs.Dispose()

        st.Close()
        wc.GetResponse().Close()

    except WebException, ex:
        printException(ex)


url = "http://localhost/conpdata/"
postUrl = url + "default.wmv"

wc = WebRequest.Create(postUrl)

ccache = CredentialCache()
ccache.Add(Uri(url), "Digest", NetworkCredential("test", "testpass"))
wc.Credentials = ccache

#存在しない URL に HEAD メソッドは使えないため、事前に空のファイルを作成
wc.PreAuthenticate = True
wc.Method = "PUT"
wc.GetRequestStream().Close()
wc.GetResponse().Close()


wc = WebRequest.Create(postUrl)
wc.Credentials = ccache

#AllowWriteStreamBuffering を false にして有効に通信するためには
#PreAuthenticate で事前認証済みでなければならない
wc.AllowWriteStreamBuffering = False
wc.PreAuthenticate = True
wc.Method = "PUT"

#ファイル配置
put_file(wc, "default.wmv")
